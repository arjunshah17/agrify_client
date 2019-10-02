package com.example.agrify.activity.order;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.agrify.R;

import com.example.agrify.activity.MainActivity;
import com.example.agrify.activity.model.Seller;
import com.example.agrify.activity.model.User;
import com.example.agrify.activity.order.adapter.OrderAdapter;
import com.example.agrify.activity.order.adapter.OrderItemAdapter;
import com.example.agrify.activity.order.model.Order;
import com.example.agrify.activity.order.model.OrderItem;
import com.example.agrify.activity.sellerProduct.SellerAddressFragment;
import com.example.agrify.activity.sellerProduct.adpater.CartAdapter;
import com.example.agrify.databinding.ActivityOrderDetailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import javax.annotation.Nullable;

import es.dmoral.toasty.Toasty;
import io.opencensus.tags.Tag;

public class OrderDetailActivity extends AppCompatActivity implements OrderItemAdapter.OnClickRateListener {
    Query query;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    private File pdfFile;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private PdfPCell cell;
    String orderId;
    OrderItemAdapter orderItemAdapter;
    ActivityOrderDetailBinding binding;
    Order order;
    String mBuyerName,mBuyerPhone,mBuyerAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        progressLoading(true);
        binding.appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (getIntent().getStringExtra("orderId") != null) {
            orderId = getIntent().getStringExtra("orderId");
        }
        InitUi();
        initCartRecycleView();



    }

    private void initCartRecycleView() {
        query = firebaseFirestore.collection("Users").document(auth.getUid()).collection("orderList").document(orderId).collection("orderList");
        orderItemAdapter = new OrderItemAdapter(query, this, this) {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                super.onEvent(documentSnapshots, e);


                if (getItemCount() == 0) {
                    noProductFound(true);
                } else {
                    noProductFound(false);
                }


            }

            @Override
            protected void onDocumentModified(DocumentChange change) {
                super.onDocumentModified(change);
            }
        };


        binding.orderRecycleView.setLayoutManager(new LinearLayoutManager(this));
        binding.orderRecycleView.setAdapter(orderItemAdapter);
    }
String s;
    private void DownloadInvoice() throws FileNotFoundException, DocumentException {
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();

        }

        pdfFile = new File(docsFolder.getAbsolutePath(), order.getOrderId()+".pdf");
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document();
        PdfWriter.getInstance(document, output);

        // Open to write
        document.open();

        // Document Settings
        document.setPageSize(PageSize.A4);
        document.addCreationDate();
        document.addAuthor("AgrifyApp");
        document.addCreator("Ashish Sonani");
        BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
        float mHeadingFontSize = 20.0f;
        float mValueFontSize = 26.0f;
        /**
         * How to USE FONT....
         */
        BaseFont urName = null;
        try {
            urName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Paragraph paragraph = new Paragraph();
        // LINE SEPARATOR
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
        // Title Order Details...
        // Adding Title....
        Font mOrderDetailsTitleFont = new Font(urName, 36.0f, Font.NORMAL, BaseColor.BLACK);
        Chunk mOrderDetailsTitleChunk = new Chunk("Agrify Order Details", mOrderDetailsTitleFont);
        Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
        mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(mOrderDetailsTitleParagraph);
        // Adding Line Breakable Space....
        document.add(new Paragraph(""));
        // Adding Horizontal Line...
        document.add(new Chunk(lineSeparator));
        // Adding Line Breakable Space....
        document.add(new Paragraph(""));

        paragraph = new Paragraph("Customer Support: 9943123999");
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        paragraph = new Paragraph("Email :  agrifyapp@gmail.com");
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
        document.add(new Chunk(lineSeparator));

        // a table with three columns
        PdfPTable table = new PdfPTable(3);
        Font mUserFont = new Font(urName, 12.0f, Font.BOLD, BaseColor.BLACK);
        firebaseFirestore.collection("Users").document(order.getUserId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                User user=snapshot.toObject(User.class);
                //objects are arlready created,no need to create new object

                mBuyerName = user.getName();
                mBuyerPhone = user.getPhone();
                mBuyerAddress= order.getUserHouseNum()+order.getUserLocation();
//                PdfPCell BuyerDetailsCell = new PdfPCell();
//                Paragraph BuyerDetails = new Paragraph("Buyer Details",mUserFont);
//                BuyerDetails.setAlignment(Element.ALIGN_CENTER);
//                BuyerDetailsCell.setColspan(3);
//                BuyerDetailsCell.addElement(BuyerDetails);
//                table.addCell(BuyerDetailsCell).setBorder(Rectangle.NO_BORDER);
//                try {
//                    document.add(new Chunk(lineSeparator));
//                } catch (DocumentException e) {
//                    e.printStackTrace();
//                }
//                //Buyer Name
//                PdfPCell BuyerNameCell = new PdfPCell();
//                Paragraph BuyerName = new Paragraph(user.getName(),mUserFont);
//                BuyerNameCell.addElement(BuyerName);
//                BuyerNameCell.setColspan(3);
//                table.addCell(BuyerNameCell).setBorder(Rectangle.NO_BORDER);
//
//                //Buyer Number
//                PdfPCell BuyerNumberCell = new PdfPCell();
//                Paragraph BuyerNumber = new Paragraph(user.getPhone(),mUserFont);
//                BuyerNumberCell.addElement(BuyerNumber);
//                BuyerNumberCell.setColspan(3);
//                table.addCell(BuyerNumberCell).setBorder(Rectangle.NO_BORDER);
//
//                //Buyer Address
//                PdfPCell BuyerAddressCell = new PdfPCell();
//                Paragraph BuyerAddress = new Paragraph(order.getUserHouseNum()+order.getUserLocation());
//                BuyerAddressCell.addElement(BuyerAddress);
//                BuyerAddressCell.setColspan(3);
//                table.addCell(BuyerAddressCell).setBorder(Rectangle.NO_BORDER);
//

                //TODO use user,seller and order object to initilize invoice header
            }
        });

        PdfPCell BuyerDetailsCell = new PdfPCell();
        Paragraph BuyerDetails = new Paragraph("Buyer Details", mUserFont);
        BuyerDetails.setAlignment(Element.ALIGN_CENTER);
        BuyerDetailsCell.setColspan(3);
        BuyerDetailsCell.addElement(BuyerDetails);
        table.addCell(BuyerDetailsCell).setBorder(Rectangle.NO_BORDER);
        document.add(new Chunk(lineSeparator));
        //Buyer Name
        PdfPCell BuyerNameCell = new PdfPCell();
        Paragraph BuyerName = new Paragraph("Buyer Name : " + mBuyerName);
        BuyerNameCell.addElement(BuyerName);
        BuyerNameCell.setColspan(3);
        table.addCell(BuyerNameCell).setBorder(Rectangle.NO_BORDER);

        //Buyer Number
        PdfPCell BuyerNumberCell = new PdfPCell();
        Paragraph BuyerNumber = new Paragraph("Buyer Phone N0 : " + mBuyerPhone);
        BuyerNumberCell.addElement(BuyerNumber);
        BuyerNumberCell.setColspan(3);
        table.addCell(BuyerNumberCell).setBorder(Rectangle.NO_BORDER);

        //Buyer Address
        PdfPCell BuyerAddressCell = new PdfPCell();
        Paragraph BuyerAddress = new Paragraph("Buyer Address : " + mBuyerAddress);
        BuyerAddressCell.addElement(BuyerAddress);
        BuyerAddressCell.setColspan(3);
        table.addCell(BuyerAddressCell).setBorder(Rectangle.NO_BORDER);

        //Seller Details
        PdfPCell SellerDetailsCell = new PdfPCell();
        Paragraph SellerDetails = new Paragraph("Seller Details",mUserFont);
        SellerDetails.setAlignment(Element.ALIGN_CENTER);
        SellerDetailsCell.setColspan(3);
        SellerDetailsCell.addElement(SellerDetails);
        table.addCell(SellerDetailsCell).setBorder(Rectangle.NO_BORDER);
        document.add(new Chunk(lineSeparator));

        //Seller Name
        PdfPCell SellerNameCell = new PdfPCell();
        Paragraph SellerName = new Paragraph("Seller Name : "+seller.getName());
        SellerNameCell.addElement(SellerName);
        SellerNameCell.setColspan(3);
        table.addCell(SellerNameCell).setBorder(Rectangle.NO_BORDER);

        //Seller Number
        PdfPCell SellerNumberCell = new PdfPCell();
        Paragraph SellerNumber = new Paragraph("Seller No. : "+seller.getPhone());
        SellerNumberCell.addElement(SellerNumber);
        SellerNumberCell.setColspan(3);
        table.addCell(SellerNumberCell).setBorder(Rectangle.NO_BORDER);

        //OrderId
        PdfPCell EmptyCell = new PdfPCell();
        Paragraph emptyrow = new Paragraph(" ");
        EmptyCell.setColspan(3);
        EmptyCell.addElement(emptyrow);
        table.addCell(EmptyCell).setBorder(Rectangle.NO_BORDER);

        //OrderId
        PdfPCell OrderIdCell = new PdfPCell();
        Paragraph orderID = new Paragraph("OrderID : "+order.getOrderId());
        OrderIdCell.setColspan(3);
        OrderIdCell.addElement(orderID);
        table.addCell(OrderIdCell);

        // OrderDate
        PdfPCell OrderDateCell = new PdfPCell();
        Paragraph orderDate = new Paragraph("Date : "+order.getTimestamp().toDate().toString());
        OrderDateCell.setColspan(3);
        OrderDateCell.addElement(orderDate);
        table.addCell(OrderDateCell);

        //Cell Product
        PdfPCell ProductName = new PdfPCell();
        Paragraph productName = new Paragraph("Product");
        ProductName.addElement(productName);
        table.addCell(ProductName);

        //Cell Qty
        PdfPCell ProductQty = new PdfPCell();
        Paragraph productQty = new Paragraph("Qty");
        ProductQty.addElement(productQty);
        table.addCell(ProductQty);

        //Cell amount
        PdfPCell ProductAmount = new PdfPCell();
        Paragraph productAmount = new Paragraph("Amount");
        ProductAmount.addElement(productAmount);
        table.addCell(ProductAmount);

        for (int i = 0; i < orderItemAdapter.getItemCount(); i++) {
            OrderItem orderItem = orderItemAdapter.getOrderItem(i);
            //object for product information

            table.addCell(orderItem.getName());
            table.addCell(String.valueOf(orderItem.getQuantity()));
            table.addCell(String.valueOf(orderItem.getPrice()));
        }


        Chunk mSubTotal = new Chunk("Total :"+order.getTotalAmount());
        Paragraph SubTotal = new Paragraph(mSubTotal);
        SubTotal.setAlignment(Element.ALIGN_RIGHT);
        PdfPCell subTotalCell = new PdfPCell();
        subTotalCell.setColspan(3);
        subTotalCell.addElement(SubTotal);
        table.addCell(subTotalCell);
        document.add(table);
        document.close();



    }

    private void InitUi() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy");
        firebaseFirestore.collection("Users").document(auth.getUid()).collection("orderList").document(orderId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                try {
                    {

                        order = snapshot.toObject(Order.class);
                        binding.orderId.setText(order.getOrderId());
                        binding.orderDate.setText(simpleDateFormat.format(order.getTimestamp().toDate()));
                        binding.orderStatus.setText(order.getOrderStatus());
                        binding.addressNameTv.setText(order.getUserAddressname());
                        binding.addressLocation.setText(order.getUserHouseNum() + order.getUserLocation());
                        binding.totalAmount.setText("₹"+NumberFormat.getInstance().format(order.getTotalAmount()));
                        initSellerDetails();

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
    }

    private void noProductFound(boolean b) {
    }



    @Override
    protected void onStop() {
        super.onStop();
        if (orderItemAdapter != null) {
            orderItemAdapter.stopListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (orderItemAdapter != null) {
            orderItemAdapter.startListening();
        }
    }

    @Override
    public void onClikedRating(DocumentSnapshot snapshot, OrderItem orderItem) {
        RatingDialogFragment fragment = new RatingDialogFragment(snapshot, orderItem);
        fragment.show(getSupportFragmentManager(), "OrderDetailActivity");

    }
    Seller seller;
    void initSellerDetails() {
       firebaseFirestore.collection("Sellers").document( order.getSellerId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
           @Override
           public void onSuccess(DocumentSnapshot snapshot) {

               progressLoading(false);
                seller=snapshot.toObject(Seller.class);
               binding.userName.setText(seller.getName());
               binding.downloadInvoice.setOnClickListener(v -> {

                   int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                   if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {

                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                           if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                               showMessageOKCancel("You need to allow access to Storage",
                                       new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                                                   return;
                                               }
                                               requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                       REQUEST_CODE_ASK_PERMISSIONS);
                                           }
                                       });
                               return;
                           }


                           requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                   REQUEST_CODE_ASK_PERMISSIONS);
                       }
                       return;
                   } else {
                       try {
                           DownloadInvoice();
                       } catch (FileNotFoundException e) {
                           e.printStackTrace();
                       } catch (DocumentException e) {
                           e.printStackTrace();
                       }
                   }


               });
               try {


                   Glide.with(getApplicationContext())
                           .load(seller.getProfilePhotoUrl())
                           .into(binding.profilePhoto);
               } catch (Exception ex) {

               }

               binding.call.setOnClickListener(v -> {
                   Intent callIntent = new Intent(Intent.ACTION_DIAL);
                   callIntent.setData(Uri.parse("tel:" + seller.getPhone()));
                   try {


                       startActivity(callIntent);
                   } catch (Exception ex) {
                       ex.printStackTrace();
                   }

               });
               binding.email.setOnClickListener(v -> {
                   Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                   emailIntent.setData(Uri.parse("mailto:" + seller.getEmail()));
                   try {


                       startActivity(emailIntent);
                   } catch (Exception ex) {
                       ex.printStackTrace();
                   }

               });
           }
       });

    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    try {
                        DownloadInvoice();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Permission Denied
                    Toast.makeText(this, "WRITE_EXTERNAL Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    void progressLoading(boolean state) {
        if (state) {
            binding.progressBarLayout.progressBarLoader.setVisibility(View.VISIBLE);
            binding.mainLayout.setVisibility(View.GONE);
        } else {
            binding.progressBarLayout.progressBarLoader.setVisibility(View.GONE);
            binding.mainLayout.setVisibility(View.VISIBLE);
        }

    }
}
