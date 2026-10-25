package com.example.e_commerceapplication.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import com.example.e_commerceapplication.models.Order;
import com.example.e_commerceapplication.models.OrderItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Generates a PDF e-Receipt for an order and saves it to external storage/downloads.
 */
public class PdfGenerator {

    public static File generateReceiptPdf(Context context, Order order) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        // Header Title
        titlePaint.setColor(Color.parseColor("#00A859")); // M-Pesa Green
        titlePaint.setTextSize(24f);
        titlePaint.setFakeBoldText(true);
        canvas.drawText("ShopMpesa Retail Store", 40, 50, titlePaint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(14f);
        canvas.drawText("Official E-Receipt & Payment Confirmation", 40, 75, paint);

        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(1f);
        canvas.drawLine(40, 90, 550, 90, paint);

        // Order & Customer Details
        paint.setColor(Color.BLACK);
        paint.setTextSize(12f);
        int y = 120;
        canvas.drawText("Order Ref: " + order.getOrderReference(), 40, y, paint);
        canvas.drawText("M-Pesa Receipt: " + (order.getMpesaReceiptNumber() != null ? order.getMpesaReceiptNumber() : "N/A"), 320, y, paint);

        y += 20;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US);
        canvas.drawText("Date: " + sdf.format(new Date(order.getOrderDate())), 40, y, paint);
        canvas.drawText("Customer: " + order.getCustomerName(), 320, y, paint);

        y += 20;
        canvas.drawText("Phone: " + order.getCustomerPhone(), 40, y, paint);
        canvas.drawText("Delivery: " + order.getDeliveryType(), 320, y, paint);

        y += 30;
        paint.setColor(Color.parseColor("#00A859"));
        paint.setTextSize(14f);
        paint.setFakeBoldText(true);
        canvas.drawText("ORDER ITEMS", 40, y, paint);

        y += 15;
        paint.setColor(Color.BLACK);
        paint.setTextSize(12f);
        paint.setFakeBoldText(false);
        canvas.drawText("Item Name", 40, y, paint);
        canvas.drawText("Qty", 320, y, paint);
        canvas.drawText("Price (KES)", 400, y, paint);
        canvas.drawText("Subtotal", 480, y, paint);

        y += 10;
        canvas.drawLine(40, y, 550, y, paint);

        y += 20;
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                canvas.drawText(item.getProductName(), 40, y, paint);
                canvas.drawText(String.valueOf(item.getQuantity()), 320, y, paint);
                canvas.drawText(String.format(Locale.US, "%.2f", item.getUnitPrice()), 400, y, paint);
                canvas.drawText(String.format(Locale.US, "%.2f", item.getSubtotal()), 480, y, paint);
                y += 20;
            }
        }

        y += 10;
        canvas.drawLine(40, y, 550, y, paint);

        y += 25;
        paint.setFakeBoldText(true);
        canvas.drawText(String.format(Locale.US, "TOTAL PAID: KES %.2f", order.getTotalAmount()), 320, y, paint);

        y += 40;
        paint.setColor(Color.DKGRAY);
        paint.setTextSize(10f);
        paint.setFakeBoldText(false);
        canvas.drawText("Thank you for shopping with ShopMpesa!", 180, y, paint);

        document.finishPage(page);

        File pdfDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (pdfDir != null && !pdfDir.exists()) {
            pdfDir.mkdirs();
        }
        File pdfFile = new File(pdfDir, "Receipt_" + order.getOrderReference() + ".pdf");

        try {
            document.writeTo(new FileOutputStream(pdfFile));
            Toast.makeText(context, "Receipt saved: " + pdfFile.getName(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save PDF receipt", Toast.LENGTH_SHORT).show();
        } finally {
            document.close();
        }

        return pdfFile;
    }
}