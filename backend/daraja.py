import base64
import datetime
import requests
import os
import time

class DarajaClient:
    """
    Handles authentication and STK Push requests to Safaricom Daraja API.
    Credential secrets are kept strictly server-side.
    """
    def __init__(self, consumer_key, consumer_secret, shortcode, passkey, callback_url, env="sandbox"):
        self.consumer_key = consumer_key
        self.consumer_secret = consumer_secret
        self.shortcode = shortcode
        self.passkey = passkey
        self.callback_url = callback_url
        self.env = env

        self.base_url = "https://sandbox.safaricom.co.ke" if env == "sandbox" else "https://api.safaricom.co.ke"
        self._access_token = None
        self._token_expires_at = 0

    def get_access_token(self):
        """
        Obtains or refreshes OAuth 2.0 Access Token from Daraja.
        Caches token in memory until expiration.
        """
        if self._access_token and time.time() < self._token_expires_at:
            return self._access_token

        url = f"{self.base_url}/oauth/v1/generate?grant_type=client_credentials"
        response = requests.get(url, auth=(self.consumer_key, self.consumer_secret), timeout=15)
        response.raise_for_status()

        data = response.json()
        self._access_token = data["access_token"]
        expires_in = int(data.get("expires_in", 3599))
        self._token_expires_at = time.time() + expires_in - 60
        return self._access_token

    def _generate_password_and_timestamp(self):
        """
        Generates Base64 encoded password: Base64(Shortcode + Passkey + Timestamp)
        Timestamp format: YYYYMMDDHHmmss
        """
        timestamp = datetime.datetime.now().strftime("%Y%m%d%H%M%S")
        raw = f"{self.shortcode}{self.passkey}{timestamp}"
        password = base64.b64encode(raw.encode("utf-8")).decode("utf-8")
        return password, timestamp

    def initiate_stk_push(self, phone_number, amount, account_reference, transaction_desc="ShopMpesa Order"):
        """
        Triggers M-Pesa Express STK Push prompt on customer handset.
        """
        token = self.get_access_token()
        password, timestamp = self._generate_password_and_timestamp()

        # Format phone to 2547XXXXXXXX
        phone_clean = str(phone_number).strip().replace("+", "")

        payload = {
            "BusinessShortCode": self.shortcode,
            "Password": password,
            "Timestamp": timestamp,
            "TransactionType": "CustomerPayBillOnline",
            "Amount": int(round(amount)),
            "PartyA": phone_clean,
            "PartyB": self.shortcode,
            "PhoneNumber": phone_clean,
            "CallBackURL": self.callback_url,
            "AccountReference": account_reference,
            "TransactionDesc": transaction_desc
        }

        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        }

        url = f"{self.base_url}/mpesa/stkpush/v1/processrequest"
        response = requests.post(url, json=payload, headers=headers, timeout=20)
        return response.json()
