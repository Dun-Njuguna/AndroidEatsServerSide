package com.dunk.androideatsserverside;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dunk.androideatsserverside.Common.Common;
import com.dunk.androideatsserverside.Interface.ItemClickListener;
import com.dunk.androideatsserverside.ViewHolder.FoodViewHolder;
import com.dunk.androideatsserverside.model.Category;
import com.dunk.androideatsserverside.model.Food;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.hoang8f.widget.FButton;

public class FoodList extends AppCompatActivity {

    @BindView(R.id.recycler_food)
    RecyclerView recycler_food;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase db;
    DatabaseReference foodlist;
    String categoryId = "";
    FirebaseStorage storage;
    StorageReference storageReference;

    //add new food
    MaterialEditText edtNaame,edtDescription,edtPrice,edtDiscount;
    FButton btnUpload,btnSelect;

    Food newFood;
    Uri saveUri;
    private final int PICK_IMAGE_REQUEST =71;

    @BindView(R.id.rootLayout)
    RelativeLayout rootlayout;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        ButterKnife.bind(this);

        //firebase
        db = FirebaseDatabase.getInstance();
        foodlist = db.getReference("Foods");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //floating action button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFoodDialog();
            }
        });

        //loadfoodlist
        recycler_food.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_food.setLayoutManager(layoutManager);



        //recieving intent with category id
        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryId");
        }

        if (!categoryId.isEmpty() && categoryId != null) {
            loodLFoodList(categoryId);
        }

    }

    private void showAddFoodDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Add new food item");
        alertDialog.setMessage("Please fill in all information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout,null);

        edtNaame = add_menu_layout.findViewById(R.id.edtNameImage);
        edtDescription = add_menu_layout.findViewById(R.id.edtDescription);
        edtPrice = add_menu_layout.findViewById(R.id.edtPrice);
        edtDiscount = add_menu_layout.findViewById(R.id.edtDiscout);

        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);


        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //let user select image from Gallery and save uri
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        //set button
        alertDialog.setPositiveButton("Add menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(newFood != null){
                    if (newFood.getName() != null){
                        foodlist.push().setValue(newFood);
                        Snackbar.make(rootlayout,"New category" + newFood.getName()+ " was added", Snackbar.LENGTH_LONG)
                                .show();
                        newFood=null;
                    }
                } else
                    Snackbar.make(rootlayout,"Please fill all fields", Snackbar.LENGTH_LONG)
                            .show();

            }
        });


        alertDialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();

    }

    private void loodLFoodList(String categoryId){

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Foods").orderByChild("menuId").equalTo(categoryId);


        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(query, new SnapshotParser<Food>() {
                            @NonNull
                            @Override
                            public Food parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Food(
                                        snapshot.child("description").getValue().toString(),
                                        snapshot.child("discount").getValue().toString(),
                                        snapshot.child("image").getValue().toString(),
                                        snapshot.child("menuId").getValue().toString(),
                                        snapshot.child("name").getValue().toString(),
                                        snapshot.child("price").getValue().toString());

                            }
                        }).build();


        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);

                return new FoodViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(FoodViewHolder viewHolder, final int position, Food model) {
                viewHolder.food_name.setText(model.getName());
                Picasso.get().load(model.getImage()).into(viewHolder.food_image);
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onclick(View view, int position, boolean isLongClick) {
//                        //start activity to navigate to food details page
//                        Intent intent = new Intent(FoodList.this, FoodDetail.class);
//                        intent.putExtra("FoodId", adapter.getRef(position).getKey());
//                        startActivity(intent);
                    }
                });
            }

        };
        adapter.notifyDataSetChanged();
        recycler_food.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    // selecting image
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "SelectPicture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() !=null){
            saveUri=data.getData();
            btnSelect.setText("Image selected !");

        }
    }

    //uploading image
    private void uploadImage() {
        if (saveUri != null){
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading....");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(FoodList.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // add value for new Category if image upload and get download link
                                    newFood = new Food();
                                    newFood.setName(edtNaame.getText().toString());
                                    newFood.setDescription(edtDescription.getText().toString());
                                    newFood.setPrice(edtPrice.getText().toString());
                                    newFood.setDiscount(edtDiscount.getText().toString());
                                    newFood.setMenuId(categoryId);
                                    newFood.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progrees = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded " + progrees + "%");
                        }
                    });
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)){
            showUpdatedFoodDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));

        }else if (item.getTitle().equals(Common.DELETE)){
            deleteFoodItem(adapter.getRef(item.getOrder()).getKey());

        }

        return super.onContextItemSelected(item);
    }

    private void deleteFoodItem(String key) {
        foodlist.child(key).removeValue();
        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
    }

    private void showUpdatedFoodDialog(final String key, final Food item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Update Food Item");
        alertDialog.setMessage("Please fill in all information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_food_layout = inflater.inflate(R.layout.add_new_food_layout,null);

        edtNaame = add_new_food_layout.findViewById(R.id.edtNameImage);
        edtDescription = add_new_food_layout.findViewById(R.id.edtDescription);
        edtPrice = add_new_food_layout.findViewById(R.id.edtPrice);
        edtDiscount = add_new_food_layout.findViewById(R.id.edtDiscout);
        btnSelect = add_new_food_layout.findViewById(R.id.btnSelect);
        btnUpload = add_new_food_layout.findViewById(R.id.btnUpload);


        //set default name
        edtNaame.setText(item.getName());
        edtDescription.setText(item.getDescription());
        edtPrice.setText(item.getPrice());
        edtDiscount.setText(item.getDiscount());


        alertDialog.setView(add_new_food_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //let user select image from Gallery and save uri
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });

        //set button
        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setName(edtNaame.getText().toString());
                item.setDescription(edtDescription.getText().toString());
                item.setPrice(edtPrice.getText().toString());
                item.setDiscount(edtDiscount.getText().toString());
                item.setMenuId(categoryId);
                foodlist.child(key).setValue(item);

            }
        });


        alertDialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }


    private void changeImage(final Food item) {
        if (saveUri != null){
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading....");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(FoodList.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // add value for new Category if image upload and get download link
                                    item.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progrees = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded " + progrees + "%");
                        }
                    });
        }
    }


}
