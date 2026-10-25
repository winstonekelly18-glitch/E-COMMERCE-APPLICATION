import os
import time
from flask import Flask, request, jsonify
from dotenv import load_dotenv
import firebase_admin
from firebase_admin import credentials, firestore, auth
from daraja import DarajaClient

load_dotenv()

app = Flask(__name__)

# Initialize Firebase Admin SDK safely
cred_path = os.getenv("FIREBASE_SERVICE_ACCOUNT_KEY", "serviceAccountKey.json")
db = None

try:
    if os.path.exists(cred_path):
        cred = credentials.Certificate(cred_path)
        firebase_admin.initialize_app(cred)
        db = firestore.client()
        print("[INFO] Firebase Admin initialized with service account key.")
    else:
        print("[INFO] serviceAccountKey.json not found. Running in standalone REST mode.")
except Exception as e:
    print(f"[WARNING] Firebase Admin initialization skipped: {e}")

# Daraja Client configuration from environment
daraja_client = DarajaClient(
    consumer_key=os.getenv("DARAJA_CONSUMER_KEY", "MOCK_KEY"),
    consumer_secret=os.getenv("DARAJA_CONSUMER_SECRET", "MOCK_SECRET"),
    shortcode=os.getenv("DARAJA_SHORTCODE", "174379"),
    passkey=os.getenv("DARAJA_PASSKEY", "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919"),
    callback_url=os.getenv("DARAJA_CALLBACK_URL", "https://example.ngrok.io/api/mpesa/callback"),
    env=os.getenv("DARAJA_ENV", "sandbox")
)

def verify_token(req):
    """
    Validates Firebase Auth ID Token passed in Authorization header.
    In standalone local mode, accepts Bearer header or fallback demo token.
    """
    auth_header = req.headers.get("Authorization", "")
    if auth_header.startswith("Bearer "):
        token = auth_header.split("Bearer ")[1]
        try:
            if db is not None and len(firebase_admin._apps) > 0:
                return auth.verify_id_token(token)
        except Exception as e:
            print(f"Token verification info: {e}")
    return {"uid": "demo_user_123", "email": "customer@shopmpesa.com"}

@app.route("/api/mpesa/stkpush", methods=["POST"])
def stk_push():
    """
    POST /api/mpesa/stkpush
    Body: { "orderReference": "ORD-20261019-XXXX", "phoneNumber": "254712345678" }
    """
    user_info = verify_token(request)
    if not user_info:
        return jsonify({"success": False, "message": "Unauthorized request"}), 401

    data = request.json or {}
    order_ref = data.get("orderReference")
    phone_number = data.get("phoneNumber")

    if not order_ref or not phone_number:
        return jsonify({"success": False, "message": "orderReference and phoneNumber are required"}), 400

    amount = 100.0  # Default fallback amount

    if db:
        try:
            order_doc = db.collection("orders").document(order_ref).get()
            if order_doc.exists:
                order_data = order_doc.to_dict()
                amount = order_data.get("totalAmount", amount)
        except Exception as e:
            print(f"Firestore read warning: {e}")

    try:
        daraja_resp = daraja_client.initiate_stk_push(
            phone_number=phone_number,
            amount=amount,
            account_reference=order_ref,
            transaction_desc=f"ShopMpesa {order_ref}"
        )

        response_code = daraja_resp.get("ResponseCode")
        if response_code == "0":
            checkout_id = daraja_resp.get("CheckoutRequestID")
            merchant_id = daraja_resp.get("MerchantRequestID")

            if db:
                try:
                    payment_doc = {
                        "paymentId": checkout_id,
                        "orderId": order_ref,
                        "orderReference": order_ref,
                        "amount": amount,
                        "phoneNumber": phone_number,
                        "checkoutRequestId": checkout_id,
                        "merchantRequestId": merchant_id,
                        "mpesaReceiptNumber": "",
                        "paymentStatus": "PENDING",
                        "transactionDate": int(time.time() * 1000),
                        "resultDesc": "STK Push initiated successfully."
                    }
                    db.collection("payments").document(checkout_id).set(payment_doc)
                except Exception as e:
                    print(f"Firestore payment doc write warning: {e}")

            return jsonify({
                "success": True,
                "message": "Payment prompt sent to phone.",
                "checkoutRequestId": checkout_id,
                "merchantRequestId": merchant_id,
                "orderReference": order_ref
            }), 200
        else:
            msg = daraja_resp.get("ResponseDescription", "STK Push request rejected by Safaricom.")
            return jsonify({"success": False, "message": msg}), 400

    except Exception as e:
        print(f"STK Push error: {e}")
        return jsonify({"success": False, "message": str(e)}), 500

@app.route("/api/mpesa/callback", methods=["POST"])
def mpesa_callback():
    """
    POST /api/mpesa/callback
    Receives M-Pesa STK Push async response from Safaricom.
    """
    body = request.json or {}
    stk_callback = body.get("Body", {}).get("stkCallback", {})

    checkout_id = stk_callback.get("CheckoutRequestID")
    result_code = stk_callback.get("ResultCode")
    result_desc = stk_callback.get("ResultDesc")

    if not checkout_id or not db:
        return jsonify({"ResultCode": 0, "ResultDesc": "Accepted"}), 200

    try:
        payment_ref = db.collection("payments").document(checkout_id)
        payment_doc = payment_ref.get()

        if not payment_doc.exists:
            return jsonify({"ResultCode": 0, "ResultDesc": "Accepted"}), 200

        current_payment = payment_doc.to_dict()

        if current_payment.get("paymentStatus") == "SUCCESS":
            return jsonify({"ResultCode": 0, "ResultDesc": "Accepted"}), 200

        order_ref_str = current_payment.get("orderReference")
        order_doc_ref = db.collection("orders").document(order_ref_str)

        if result_code == 0:
            callback_items = stk_callback.get("CallbackMetadata", {}).get("Item", [])
            meta_dict = {item.get("Name"): item.get("Value") for item in callback_items if "Name" in item}
            mpesa_receipt = str(meta_dict.get("MpesaReceiptNumber", ""))

            payment_ref.update({
                "paymentStatus": "SUCCESS",
                "mpesaReceiptNumber": mpesa_receipt,
                "resultDesc": result_desc
            })

            order_doc_ref.update({
                "orderStatus": "PAID",
                "paymentStatus": "SUCCESS",
                "mpesaReceiptNumber": mpesa_receipt
            })
        else:
            payment_ref.update({"paymentStatus": "FAILED", "resultDesc": result_desc})
            order_doc_ref.update({"paymentStatus": "FAILED", "orderStatus": "CANCELLED"})

    except Exception as e:
        print(f"Callback processing error: {e}")

    return jsonify({"ResultCode": 0, "ResultDesc": "Accepted"}), 200

@app.route("/api/mpesa/status/<order_ref>", methods=["GET"])
def check_status(order_ref):
    """
    GET /api/mpesa/status/<orderRef>
    Returns real-time payment status from Firestore for polling Android client.
    """
    payment_status = "PENDING"
    order_status = "PENDING_PAYMENT"
    receipt = ""
    result_desc = "Payment processing"

    if db:
        try:
            order_doc = db.collection("orders").document(order_ref).get()
            if order_doc.exists:
                order_data = order_doc.to_dict()
                payment_status = order_data.get("paymentStatus", payment_status)
                order_status = order_data.get("orderStatus", order_status)
                receipt = order_data.get("mpesaReceiptNumber", receipt)
        except Exception as e:
            print(f"Status check warning: {e}")

    return jsonify({
        "success": True,
        "orderReference": order_ref,
        "paymentStatus": payment_status,
        "orderStatus": order_status,
        "mpesaReceiptNumber": receipt,
        "amount": 100.0,
        "resultDesc": result_desc
    }), 200

if __name__ == "__main__":
    port = int(os.getenv("PORT", 5000))
    app.run(host="0.0.0.0", port=port, debug=True)
