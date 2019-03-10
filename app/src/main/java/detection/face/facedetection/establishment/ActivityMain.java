package detection.face.facedetection.establishment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DecimalFormat;

import detection.face.facedetection.R;
import detection.face.facedetection.establishment.main.MainDAO;
import detection.face.facedetection.establishment.main.MainVO;

import static detection.face.facedetection.establishment.Constant.*;
import static detection.face.facedetection.establishment.Constant.ADDING;
import static detection.face.facedetection.establishment.Constant.ADD_CATEGORY_EXISTING;
import static detection.face.facedetection.establishment.Constant.ADD_CATEGORY_FAIL;
import static detection.face.facedetection.establishment.Constant.ADD_CATEGORY_SUCCESS;
import static detection.face.facedetection.establishment.Constant.ADD_ITEM_DIALOG_TITLE;
import static detection.face.facedetection.establishment.Constant.ALERT_MESSAGE_ADD_CATEGORY;
import static detection.face.facedetection.establishment.Constant.ALERT_TITLE_ADD_CATEGORY;
import static detection.face.facedetection.establishment.Constant.CATEGORY_HINT;
import static detection.face.facedetection.establishment.Constant.CHECK_CONNECTION;
import static detection.face.facedetection.establishment.Constant.COMPLETE_FIELD_VALIDATION;
import static detection.face.facedetection.establishment.Constant.EDIT_CATEGORY_DIALOG_TITLE;
import static detection.face.facedetection.establishment.Constant.EDIT_ITEM_DIALOG_TITLE;
import static detection.face.facedetection.establishment.Constant.EDIT_PROFILE_DIALOG_TITLE;
import static detection.face.facedetection.establishment.Constant.EDIT_PROFILE_ERROR;
import static detection.face.facedetection.establishment.Constant.EDIT_PROFILE_SUCCESS;
import static detection.face.facedetection.establishment.Constant.ERROR;
import static detection.face.facedetection.establishment.Constant.GETTING_INFORMATION;
import static detection.face.facedetection.establishment.Constant.ITEM_EXISTING;
import static detection.face.facedetection.establishment.Constant.MY_CAMERA_PERMISSION_CODE;
import static detection.face.facedetection.establishment.Constant.PICK_PHOTO_REQUEST_ADD;
import static detection.face.facedetection.establishment.Constant.PICK_PHOTO_REQUEST_ITEM;
import static detection.face.facedetection.establishment.Constant.PICK_PHOTO_REQUEST_PROFILE;
import static detection.face.facedetection.establishment.Constant.SUBMITTING;
import static detection.face.facedetection.establishment.Constant.SUCCESS;
import static detection.face.facedetection.establishment.Constant.WARNING;
import static detection.face.facedetection.establishment.Constant.YOUR_MENU_ITEM;
import static detection.face.facedetection.establishment.Utility.*;
import static detection.face.facedetection.establishment.Utility.hideProgressBar;
import static detection.face.facedetection.establishment.Utility.picUrl;
import static detection.face.facedetection.establishment.Utility.save;
import static detection.face.facedetection.establishment.Utility.showAlertDialogBox;
import static detection.face.facedetection.establishment.Utility.showProgressBar;
import static detection.face.facedetection.establishment.Utility.showToastMessageLong;
import static detection.face.facedetection.establishment.Utility.showToastMessageShort;

public class ActivityMain extends AppCompatActivity {
    Context mContext = this;
    ImageView frontPicEdit;
    File finalFile;
    File finalFileItem;
    File finalFileAddItem;
    String oldPath;
    String oldPathItem;
    String oldPathAddItem = "";
    String newFilePath = "";
    String newFilePathFile = "";
    Spinner itemName;
    Spinner categoryName;
    EditText price;
    ImageView itemPicPath;
    Spinner statusItem;
    EditText currentItemName;
    Spinner categoryStatus;
    AlertDialog.Builder dialogBuilderEditItem;
    int itemId;
    ImageView imageAddMenu;
    ImageView frontStore;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_establishment);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void init(){
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_CAMERA_PERMISSION_CODE);
        }
        Button editProfile = findViewById(R.id.edit_profile);
        Button editMenuItem = findViewById(R.id.edit_menu_item);
        Button editCategory = findViewById(R.id.edit_categories);
        Button addCategory = findViewById(R.id.add_category);
        Button addMenu = findViewById(R.id.add_menu);
        Button viewMenu = findViewById(R.id.view_menu);
        Button logout = findViewById(R.id.logout);
        frontStore = findViewById(R.id.front_store);

        logout.setOnClickListener(v -> {
            AlertDialog.Builder ab = new AlertDialog.Builder(ActivityMain.this);
            ab.setTitle("Logging out");
            ab.setMessage("Do you want to logout as " + Utility.getString("est_name",mContext));

            ab.setPositiveButton("YES", (dialog, which) -> {
                save("est_age","",mContext);
                save("est_emotion","",mContext);
                save("est_address","",mContext);
                save("est_id","",mContext);
                save("est_name","",mContext);
                save("est_user_id","",mContext);
                save("est_front_store_url","",mContext);
                save("id","",mContext);
                save("est_location_lat","",mContext);
                save("est_location_lon","",mContext);
                save("est_est_type_id","",mContext);
                startActivity(new Intent(mContext,ActivityLogin.class));
                finish();
            });
            AlertDialog a = ab.create();
            a.show();
        });

        Picasso.with(mContext)
                .load(picUrl(Utility.getString("est_front_store_url",mContext)))
                .placeholder(R.drawable.default_image_thumbnail)
                .error(R.drawable.default_image_thumbnail)
                .into(frontStore);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MainVO vo = new MainVO();
                MainDAO.getEstInformation(Utility.getString("est_name",mContext),vo,mContext);
                showProgressBar(GETTING_INFORMATION,mContext);
                new CountDownTimer(5000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                        dialogBuilder.setTitle(EDIT_PROFILE_DIALOG_TITLE);
                        dialogBuilder.setIcon(R.drawable.ic_user);
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.edit_profile, null);
                        final EditText estName = dialogView.findViewById(R.id.est_name_edit);
                        final EditText estAddress = dialogView.findViewById(R.id.est_address_edit);
                        final EditText estLat = dialogView.findViewById(R.id.latitude_edit);
                        final EditText estLon = dialogView.findViewById(R.id.longitude_edit);
                        final Spinner emotion = dialogView.findViewById(R.id.emotion_edit);
                        final Spinner age = dialogView.findViewById(R.id.age_edit);
                        final Spinner estTypeName = dialogView.findViewById(R.id.est_type_edit);
                        frontPicEdit = dialogView.findViewById(R.id.front_store_edit);
                        Button selectFromGallery = dialogView.findViewById(R.id.select_image);
                        Button submit = dialogView.findViewById(R.id.submit_edited_profile);

                        selectFromGallery.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto , PICK_PHOTO_REQUEST_PROFILE);
                            }
                        });


                        estName.setText(vo.getEstName());
                        estAddress.setText(vo.getEstAddress());
                        estLat.setText(vo.getLocationLat());
                        estLon.setText(vo.getLocationLon());

                        ArrayAdapter<CharSequence> emotionList = ArrayAdapter.createFromResource(mContext, R.array.emotion_item, android.R.layout.simple_spinner_item);
                        ArrayAdapter<CharSequence> ageList = ArrayAdapter.createFromResource(mContext, R.array.age_range, android.R.layout.simple_spinner_item);
                        try {
                            ArrayAdapter<CharSequence> estTypeList = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, vo.get_estTypeName());
                            estTypeName.setAdapter(estTypeList);
                            estTypeName.setSelection(estTypeList.getPosition(vo.getEstTypeName()));
                        }catch (Exception e){
                            showAlertDialogBox(CHECK_CONNECTION,ERROR,mContext,ERROR);
                        }

                        emotion.setSelection(emotionList.getPosition(vo.getEmotion()));
                        age.setSelection(ageList.getPosition(vo.getAge()));

                        oldPath = vo.getFrontStoreUrl();
                        Picasso.with(mContext)
                                .load(oldPath)
                                .error(R.drawable.default_image_thumbnail)
                                .placeholder(R.drawable.default_image_thumbnail).into(frontPicEdit);


                        dialogBuilder.setView(dialogView);
                        AlertDialog alertDialog = dialogBuilder.create();
                        alertDialog.show();
                        hideProgressBar();

                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(!"".equals(estName.getText().toString()) || !"".equals(estAddress.getText().toString())||
                                !"".equals(estLat.getText().toString()) || !"".equals(estLon.getText().toString()) ||
                                !"".equals(emotion.getSelectedItem().toString()) || !"".equals(age.getSelectedItem().toString()) ||
                                !"".equals(estTypeName.getSelectedItem().toString())) {
                                    newFilePath = "";
                                    showProgressBar(SUBMITTING,mContext);
                                    File newPath = new File("");
                                    final MainVO vo = new MainVO();
                                    vo.setEstName(estName.getText().toString());
                                    vo.setEstAddress(estAddress.getText().toString());
                                    vo.setLocationLat(estLat.getText().toString());
                                    vo.setLocationLon(estLon.getText().toString());
                                    vo.setEmotion(emotion.getSelectedItem().toString());
                                    vo.setAge(age.getSelectedItem().toString());
                                    vo.setEstId(Utility.getString("est_id",mContext))  ;
                                    vo.setEstTypeName(estTypeName.getSelectedItem().toString());
                                    vo.setFrontStoreUrl(oldPath);
                                    if (!oldPath.contains("https://darkened-career.000webhostapp.com/images/")) {
                                        newFilePath = finalFile.getPath();
                                    }
                                    try {
                                        MainDAO.submitEditedProfile(vo,newFilePath, mContext);
                                        new CountDownTimer(7000, 1000) {
                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                            }

                                            @Override
                                            public void onFinish() {
                                                switch (vo.getSubmitEditProfileStatus()) {
                                                    case 1: // success
                                                        showAlertDialogBox(EDIT_PROFILE_SUCCESS, SUCCESS, mContext, SUCCESS);
                                                        break;
                                                    case 2:
                                                        showAlertDialogBox(EDIT_PROFILE_ERROR, ERROR, mContext, ERROR);
                                                        break;
                                                    default:
                                                        showAlertDialogBox(CHECK_CONNECTION, ERROR, mContext, ERROR);
                                                        break;
                                                }
                                                hideProgressBar();
                                            }
                                        }.start();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    showAlertDialogBox(COMPLETE_FIELD_VALIDATION,WARNING,mContext,WARNING);
                                }
                            }
                        });
                    }

                }.start();
            }
        });
        editMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar(GETTING_INFORMATION,mContext);
                final MainVO vo = new MainVO();
                dialogBuilderEditItem = new AlertDialog.Builder(mContext);
                dialogBuilderEditItem.setTitle(EDIT_ITEM_DIALOG_TITLE);
                dialogBuilderEditItem.setIcon(R.drawable.ic_menu);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.edit_menu, null);

                itemName = dialogView.findViewById(R.id.item_name_edit);
                categoryName = dialogView.findViewById(R.id.item_category_choose_edit);
                price = dialogView.findViewById(R.id.price_edit);
                itemPicPath = dialogView.findViewById(R.id.item_picture_edit);
                statusItem = dialogView.findViewById(R.id.item_status_edit);
                currentItemName = dialogView.findViewById(R.id.current_item_name_edit);
                Button submitEditedMenu = dialogView.findViewById(R.id.submit_edited_item);
                Button chooseItemPic = dialogView.findViewById(R.id.select_image_item_edit);

                chooseItemPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , PICK_PHOTO_REQUEST_ITEM);
                    }
                });

                submitEditedMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newFilePathFile = "";
                        if(!"".equals(price.getText().toString()) || !"".equals(currentItemName.getText().toString())){
                            vo.setItemName(currentItemName.getText().toString());
                            vo.setItemCategory(categoryName.getSelectedItem().toString());
                            vo.setPrice(Double.parseDouble(price.getText().toString()));
                            vo.setItemStatus("ACTIVE".equals(statusItem.getSelectedItem().toString()) ? "1" : "0");
                            showProgressBar(SUBMITTING,mContext);
                            if (!oldPathItem.contains("https://darkened-career.000webhostapp.com/images/")) {
                                newFilePathFile = finalFileItem.getPath();
                            }
                            MainDAO.submitEditItemMenu(vo,Utility.getString("id",mContext),newFilePathFile,mContext,itemId);
                            new CountDownTimer(5000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {

                                }

                                @Override
                                public void onFinish() {
                                    switch (vo.getSubmitItemStatus()) {
                                        case 1: // success
                                            showAlertDialogBox(EDIT_PROFILE_SUCCESS, SUCCESS, mContext, SUCCESS);
                                            break;
                                        case 2:
                                            showAlertDialogBox(EDIT_PROFILE_ERROR, ERROR, mContext, ERROR);
                                            break;
                                        default:
                                            showAlertDialogBox(CHECK_CONNECTION, ERROR, mContext, ERROR);
                                            break;
                                    }
                                    hideProgressBar();
                                }
                            }.start();
                        }else{
                            showAlertDialogBox(COMPLETE_FIELD_VALIDATION,WARNING,mContext,WARNING);
                        }
                    }
                });

                MainDAO.getProductNameByEstId(Utility.getString("id",mContext),vo,mContext,"all");
                new CountDownTimer(5000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        ArrayAdapter<CharSequence> itemNameList = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, vo.get_itemNameList());
                        final ArrayAdapter<CharSequence> categoryNameList = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, vo.get_itemCategory());
                        final ArrayAdapter<CharSequence> statusList = ArrayAdapter.createFromResource(mContext, R.array.status, android.R.layout.simple_spinner_item);
                        itemName.setAdapter(itemNameList);
                        categoryName.setAdapter(categoryNameList);
                        statusItem.setAdapter(statusList);

                        itemName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                showProgressBar("Getting information for " + parent.getItemAtPosition(position)+"...",mContext);
                                getItemInformation(parent.getItemAtPosition(position).toString(),vo,categoryNameList,statusList);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        itemName.setSelection(0);
                        getItemInformation(itemName.getSelectedItem().toString(),vo,categoryNameList,statusList);
                        dialogBuilderEditItem.setView(dialogView);
                        AlertDialog alertDialog = dialogBuilderEditItem.create();
                        alertDialog.show();
                        hideProgressBar();
                    }
                }.start();
            }
        });

        editCategory.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                showProgressBar(GETTING_INFORMATION,mContext);
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                dialogBuilder.setTitle(EDIT_CATEGORY_DIALOG_TITLE);
                dialogBuilder.setIcon(R.drawable.ic_category_add_icon);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.edit_category, null);
                final Spinner categoryItem = dialogView.findViewById(R.id.category_name_edit);
                categoryStatus = dialogView.findViewById(R.id.category_name_status_edit);
                final EditText currentCategoryName = dialogView.findViewById(R.id.current_category_name_edit);
                Button submitEditCategory = dialogView.findViewById(R.id.submit_edited_category);

                final MainVO vo = new MainVO();

                MainDAO.getCategoryByEstId(Utility.getString("id",mContext),vo,mContext,"all_active");
                new CountDownTimer(5000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        hideProgressBar();
                        final ArrayAdapter<CharSequence> statusList = ArrayAdapter.createFromResource(mContext, R.array.status, android.R.layout.simple_spinner_item);
                        final ArrayAdapter<CharSequence> categoryNameList = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, vo.get_itemCategory());
                        categoryItem.setAdapter(categoryNameList);
                        categoryStatus.setAdapter(statusList);
                        categoryItem.setSelection(0);
                        currentCategoryName.setText(categoryItem.getSelectedItem().toString());

                        getCategoryInformation(Utility.getString("id",mContext),categoryItem.getSelectedItem().toString(),vo,statusList);
                        hideProgressBar();
                        categoryItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                currentCategoryName.setText(parent.getItemAtPosition(position).toString());
                                getCategoryInformation(Utility.getString("id",mContext),categoryItem.getSelectedItem().toString(),vo,statusList);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        dialogBuilder.setView(dialogView);
                        AlertDialog alertDialog = dialogBuilder.create();
                        alertDialog.show();
                    }
                }.start();

                submitEditCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressBar(SUBMITTING, mContext);
                        if (!"".equals(currentCategoryName.getText().toString())) {
                            vo.setCategoryName(currentCategoryName.getText().toString());
                            vo.setCategoryStatus(categoryStatus.getSelectedItem().toString());
                            MainDAO.submitEditedCategory(vo, Utility.getString("id", mContext), mContext);
                            new CountDownTimer(5000, 1000) {

                                @Override
                                public void onTick(long millisUntilFinished) {

                                }

                                @Override
                                public void onFinish() {
                                    hideProgressBar();
                                    switch (vo.getCategoryEditStatus()) {
                                        case 1:
                                            showAlertDialogBox(SUCCESS, SUCCESS, mContext, SUCCESS);
                                            break;
                                        case 2:
                                            showAlertDialogBox(ERROR, ERROR, mContext, ERROR);
                                            break;
                                        default:
                                            showAlertDialogBox(CHECK_CONNECTION, ERROR, mContext, ERROR);
                                            break;
                                    }
                                }
                            }.start();
                        }else{
                            showToastMessageShort(COMPLETE_FIELD_VALIDATION,mContext);
                        }
                    }
                });

            }
        });

        addCategory.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle(ALERT_TITLE_ADD_CATEGORY);
                alert.setMessage(ALERT_MESSAGE_ADD_CATEGORY);
                alert.setIcon(R.drawable.ic_category_add_icon);
                LinearLayout container = new LinearLayout(mContext);
                container.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(35, 10, 35, 10);
                final EditText category = new EditText (mContext);
                category.setBackground(getResources().getDrawable(R.drawable.text_background_design));
                category.setLayoutParams(lp);
                category.setHint(CATEGORY_HINT);
                category.setHintTextColor(Color.parseColor("#000000"));
                category.setPadding(20,10,20,10);
                container.addView(category);
                alert.setView(container);

                alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String categoryName = category.getText().toString();
                        if(!"".equals(categoryName)){
                            showProgressBar(ADDING,mContext);
                            final MainVO vo = new MainVO();
                            MainDAO.submitCategory(categoryName,Utility.getString("est_user_id",mContext),vo,mContext);
                            new CountDownTimer(5000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {
                                    switch(vo.getSubmitCategoryStatus()){
                                        case 1 : // success
                                            showAlertDialogBox(ADD_CATEGORY_SUCCESS,SUCCESS,mContext,SUCCESS);
                                            break;
                                        case 2 : // existing
                                            showAlertDialogBox(ADD_CATEGORY_EXISTING,WARNING,mContext,WARNING);
                                            break;
                                        case 3 : // fail
                                            showAlertDialogBox(ADD_CATEGORY_FAIL,WARNING,mContext,WARNING);
                                            break;
                                        default:
                                            showAlertDialogBox(CHECK_CONNECTION,ERROR,mContext,ERROR);
                                            break;
                                    }
                                    hideProgressBar();
                                }
                            }.start();
                        }else{
                            showToastMessageLong(COMPLETE_FIELD_VALIDATION,mContext);
                        }
                    }
                });
                alert.show();
            }
        });

        addMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MainVO vo = new MainVO();
                final AlertDialog.Builder dialogBuilderAddItem = new AlertDialog.Builder(mContext);
                dialogBuilderAddItem.setTitle(ADD_ITEM_DIALOG_TITLE);
                dialogBuilderAddItem.setIcon(R.drawable.ic_menu);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.add_menu, null);

                final EditText itemNameAdd = dialogView.findViewById(R.id.item_name_add);
                final Spinner itemCategoryAdd = dialogView.findViewById(R.id.item_category_choose_add);
                final EditText priceAdd = dialogView.findViewById(R.id.price_add);
                imageAddMenu = dialogView.findViewById(R.id.item_picture_add);
                final Button selectImageAddItem = dialogView.findViewById(R.id.select_image_item_add);
                final Button submitButtonAdd = dialogView.findViewById(R.id.submit_add_item);

                MainDAO.getCategoryByEstId(Utility.getString("id",mContext),vo,mContext,"all_active");
                showProgressBar(GETTING_INFORMATION,mContext);
                new CountDownTimer(5000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        try {
                            hideProgressBar();
                            selectImageAddItem.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(pickPhoto, PICK_PHOTO_REQUEST_ADD);
                                }
                            });
                            submitButtonAdd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(!"".equals(itemNameAdd.getText().toString()) &&
                                            !"".equals(itemCategoryAdd.getSelectedItem().toString())&&
                                            !"".equals(priceAdd.getText().toString())&&
                                            !"".equals(oldPathAddItem)){
                                        showProgressBar(SUBMITTING,mContext);
                                        MainDAO.submitAddProduct(Utility.getString("id",mContext),
                                                itemNameAdd.getText().toString(),
                                                oldPathAddItem,
                                                itemCategoryAdd.getSelectedItem().toString(),
                                                priceAdd.getText().toString(),
                                                vo,
                                                mContext);
                                        new CountDownTimer(5000, 1000) {
                                            @Override
                                            public void onTick(long millisUntilFinished) {

                                            }

                                            @Override
                                            public void onFinish() {
                                                switch (vo.getAddItemResponseStatus()){
                                                    case 1:
                                                        showAlertDialogBox(SUCCESS,SUCCESS,mContext,SUCCESS);
                                                        break;
                                                    case 2:
                                                        showAlertDialogBox(ITEM_EXISTING,WARNING,mContext,WARNING);
                                                        break;
                                                    case 3:
                                                        showAlertDialogBox(ERROR,ERROR,mContext,ERROR);
                                                        break;
                                                    default:
                                                        showAlertDialogBox(ERROR,ERROR,mContext,ERROR);
                                                        break;
                                                }
                                                hideProgressBar();
                                            }
                                        }.start();

                                    }else{
                                        showAlertDialogBox(COMPLETE_FIELD_VALIDATION,WARNING,mContext,WARNING);
                                    }
                                }
                            });
                            ArrayAdapter<CharSequence> categoryAddItem = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, vo.get_itemCategory());
                            itemCategoryAdd.setAdapter(categoryAddItem);
                            dialogBuilderAddItem.setView(dialogView);
                            AlertDialog alertDialog = dialogBuilderAddItem.create();
                            alertDialog.show();
                        }catch (Exception e){
                            showAlertDialogBox(CHECK_CONNECTION,ERROR,mContext,ERROR);
                        }

                    }
                }.start();
//                hideProgressBar();
                //redirect tot add menu
            }
        });
        viewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MainVO vo = new MainVO();

                final AlertDialog.Builder dialogBuilderAddItem = new AlertDialog.Builder(mContext);
                dialogBuilderAddItem.setTitle(YOUR_MENU_ITEM);
                dialogBuilderAddItem.setIcon(R.drawable.ic_menu);
                final LayoutInflater inflater = getLayoutInflater();
                final LayoutInflater layout = LayoutInflater.from(mContext);
                final View dialogView = inflater.inflate(R.layout.view_menu, null);

                final LinearLayout linearLayout = dialogView.findViewById(R.id.linear_view_menu);

                showProgressBar(GETTING_INFORMATION,mContext);
                MainDAO.getAllProductOfEst(Utility.getString("id",mContext),mContext,vo);
                new CountDownTimer(5000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {}

                    @Override
                    public void onFinish() {
                        hideProgressBar();
                        for (int x = 0; x < vo.getViewMenuStatus(); x++){
                            View view = layout.inflate(R.layout.menu_view_layout,linearLayout,false);

                            String[] picturePath;
                            String[] itemName;
                            String[] itemPrice;
                            String[] itemCategory;

                            picturePath = vo.get_itemPicturePathListView();
                            itemName = vo.get_itemNameListView();
                            itemPrice = vo.get_itemPriceListView();
                            itemCategory = vo.get_itemCategoryListView();

                            ImageView productImageView = view.findViewById(R.id.product_image_view);
                            TextView itemNameView = view.findViewById(R.id.product_name_view);
                            TextView itemCategoryView = view.findViewById(R.id.product_cat_view);
                            TextView itemPriceView = view.findViewById(R.id.product_price_view);
                            Picasso.with(mContext)
                                    .load(picUrl(picturePath[x]))
                                    .placeholder(R.drawable.default_image_thumbnail)
                                    .into(productImageView);

                            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                            decimalFormat.setGroupingUsed(true);
                            decimalFormat.setGroupingSize(3);
                            itemNameView.setText(itemName[x]);
                            itemPriceView.setText("Php " + decimalFormat.format(Double.parseDouble(itemPrice[x])));
                            itemCategoryView.setText(itemCategory[x]);

                            linearLayout.addView(view);
                        }
                        dialogBuilderAddItem.setView(dialogView);
                        AlertDialog alertDialog = dialogBuilderAddItem.create();
                        alertDialog.show();
                    }
                }.start();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            for(int x = 0; x < permissions.length; x++) {
                if (grantResults[x] == PackageManager.PERMISSION_GRANTED) {

                } else {
                }
            }
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_PHOTO_REQUEST_PROFILE && resultCode == Activity.RESULT_OK){
            Uri selectedImage = data.getData();

            Picasso.with(mContext)
                    .load(selectedImage)
                    .error(R.drawable.default_image_thumbnail)
                    .placeholder(R.drawable.default_image_thumbnail)
                    .into(frontPicEdit);
            finalFile = new File(getRealPathFromURI(selectedImage));
            oldPath = finalFile.getPath();

        }
        if(requestCode == PICK_PHOTO_REQUEST_ITEM && resultCode == Activity.RESULT_OK){
            Uri selectedImage = data.getData();

            Picasso.with(mContext)
                    .load(selectedImage)
                    .error(R.drawable.default_image_thumbnail)
                    .placeholder(R.drawable.default_image_thumbnail)
                    .into(itemPicPath);
            finalFileItem = new File(getRealPathFromURI(selectedImage));
            oldPathItem = finalFileItem.getPath();

        }
        if(requestCode == PICK_PHOTO_REQUEST_ADD && resultCode == Activity.RESULT_OK){
            Uri selectedImage = data.getData();

            Picasso.with(mContext)
                    .load(selectedImage)
                    .error(R.drawable.default_image_thumbnail)
                    .placeholder(R.drawable.default_image_thumbnail)
                    .into(imageAddMenu);
            finalFileAddItem = new File(getRealPathFromURI(selectedImage));
            oldPathAddItem = finalFileAddItem.getPath();
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    public void getItemInformation(final String itemName, final MainVO vo, final ArrayAdapter cat, final ArrayAdapter status){
        MainDAO.getItemInformation(Utility.getString("id", mContext), itemName, vo, mContext);
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                decimalFormat.setGroupingUsed(true);
                decimalFormat.setGroupingSize(3);

                hideProgressBar();
                categoryName.setSelection(cat.getPosition(vo.getItemCategory()));
                statusItem.setSelection(status.getPosition(vo.getItemStatus()));
                price.setText(decimalFormat.format(vo.getPrice()));
                currentItemName.setText(itemName);
                itemId = Integer.parseInt(vo.getItemId());
                oldPathItem = vo.getItemPicPath();
                Picasso.with(mContext)
                        .load(oldPathItem)
                        .placeholder(R.drawable.default_image_thumbnail)
                        .error(R.drawable.default_image_thumbnail)
                        .into(itemPicPath);
            }
        }.start();
    }
    public void getCategoryInformation(String estId, String categoryName, final MainVO vo, final ArrayAdapter status){
        showProgressBar("Getting information for " + categoryName + "...",mContext);
        MainDAO.getCategoryInformation(estId, categoryName, vo, mContext);
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                hideProgressBar();
                categoryStatus.setSelection(status.getPosition(vo.getCategoryStatus()));
            }
        }.start();
    }

}
