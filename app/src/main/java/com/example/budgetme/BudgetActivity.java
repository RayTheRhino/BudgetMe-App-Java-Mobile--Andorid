package com.example.budgetme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class BudgetActivity extends AppCompatActivity {

    private FloatingActionButton budget_BTN_fab;
    private MaterialTextView budget_LBL_totalBudgetAmount;
    private RecyclerView recyclerView;
    private DatabaseReference budgetRef, personalRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;

    private String post_key = "";
    private String item = "";
    private int amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        findViews();
        clickListners();

        mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());
        personalRef = FirebaseDatabase.getInstance().getReference().child("personal").child(mAuth.getCurrentUser().getUid());
        loader = new ProgressDialog(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Data data = snap.getValue(Data.class);
                    totalAmount += data.getAmount();
                    String sTotal = String.valueOf("Month budget: $" + totalAmount);
                    budget_LBL_totalBudgetAmount.setText(sTotal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    int totalamount = 0;
                    for(DataSnapshot ds :snapshot.getChildren()){
                        Data data = ds.getValue(Data.class);
                        totalamount+=data.getAmount();
                        String sTotal = String.valueOf("Month Budget: "+ totalamount);
                        budget_LBL_totalBudgetAmount.setText(sTotal);
                    }
                    int weeklyBudget = totalamount/4;
                    int dailyBudget = totalamount/30;
                    personalRef.child("dailyBudget").setValue(totalamount);
                    personalRef.child("weeklyBudget").setValue(weeklyBudget);
                    personalRef.child("monthliyBudget").setValue(dailyBudget);
                }else{
                    personalRef.child("dailyBudget").setValue(0);
                    personalRef.child("weeklyBudget").setValue(0);
                    personalRef.child("monthliyBudget").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getMonthTransportBadget();
        getMonthFoodBudgetRatios();
        getMonthHouseBudgetRatios();
        getMonthEntBudgetRatios(); //Entertainment
        getMonthEduRatios(); //Education
        getMonthChairatyRatios();
        getMonthAppRatios(); //apparel
        getMonthHealthRatios();
        getMonthPerRatios(); //personal
        getMonthOtherRatios();

    }

    private void getMonthOtherRatios() {
        Query query = budgetRef.orderByChild("item").equalTo("Other");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));

                    }
                    int dayOtherRatio = pTotal / 30;
                    int weekOtherRatio = pTotal / 4;
                    int monthOtherRatio = pTotal;

                    personalRef.child("dayOtherRatio").setValue(dayOtherRatio);
                    personalRef.child("weekOtherRatio").setValue(weekOtherRatio);
                    personalRef.child("monthOtherRatio").setValue(monthOtherRatio);
                } else {
                    personalRef.child("dayOtherRatio").setValue(0);
                    personalRef.child("weekOtherRatio").setValue(0);
                    personalRef.child("monthOtherRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthPerRatios() {
        Query query = budgetRef.orderByChild("item").equalTo("Personal");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));

                    }
                    int dayPersonalRatio = pTotal / 30;
                    int weekPersonalRatio = pTotal / 4;
                    int monthPersonalRatio = pTotal;

                    personalRef.child("dayPersonalRatio").setValue(dayPersonalRatio);
                    personalRef.child("weekPersonalRatio").setValue(weekPersonalRatio);
                    personalRef.child("monthPersonalRatio").setValue(monthPersonalRatio);
                } else {
                    personalRef.child("dayPersonalRatio").setValue(0);
                    personalRef.child("weekPersonalRatio").setValue(0);
                    personalRef.child("monthPersonalRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthHealthRatios() {
        Query query = budgetRef.orderByChild("item").equalTo("Health");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));

                    }
                    int dayHealthRatio = pTotal / 30;
                    int weekHealthRatio = pTotal / 4;
                    int monthHealthRatio = pTotal;

                    personalRef.child("dayHealthRatio").setValue(dayHealthRatio);
                    personalRef.child("weekHealthRatio").setValue(weekHealthRatio);
                    personalRef.child("monthHealthRatio").setValue(monthHealthRatio);
                } else {
                    personalRef.child("dayHealthRatio").setValue(0);
                    personalRef.child("weekHealthRatio").setValue(0);
                    personalRef.child("monthHealthRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthAppRatios() {
        Query query = budgetRef.orderByChild("item").equalTo("Apparel");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));

                    }
                    int dayApparelRatio = pTotal / 30;
                    int weekApparelRatio = pTotal / 4;
                    int monthApparelRatio = pTotal;

                    personalRef.child("dayApparelRatio").setValue(dayApparelRatio);
                    personalRef.child("weekApparelRatio").setValue(weekApparelRatio);
                    personalRef.child("monthApparelRatio").setValue(monthApparelRatio);
                } else {
                    personalRef.child("dayApparelRatio").setValue(0);
                    personalRef.child("weekApparelRatio").setValue(0);
                    personalRef.child("monthApparelRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthChairatyRatios() {
        Query query = budgetRef.orderByChild("item").equalTo("Charity");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));

                    }
                    int dayCharityRatio = pTotal / 30;
                    int weekCharityRatio = pTotal / 4;
                    int monthCharityRatio = pTotal;

                    personalRef.child("dayCharityRatio").setValue(dayCharityRatio);
                    personalRef.child("weekCharityRatio").setValue(weekCharityRatio);
                    personalRef.child("monthCharityRatio").setValue(monthCharityRatio);
                } else {
                    personalRef.child("dayCharityRatio").setValue(0);
                    personalRef.child("weekCharityRatio").setValue(0);
                    personalRef.child("monthCharityRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthEduRatios() {
        Query query = budgetRef.orderByChild("item").equalTo("Education");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));

                    }
                    int dayEducationRatio = pTotal / 30;
                    int weekEducationRatio = pTotal / 4;
                    int monthEducationRatio = pTotal;

                    personalRef.child("dayEducationRatio").setValue(dayEducationRatio);
                    personalRef.child("weekEducationRatio").setValue(weekEducationRatio);
                    personalRef.child("monthEducationRatio").setValue(monthEducationRatio);
                } else {
                    personalRef.child("dayEducationRatio").setValue(0);
                    personalRef.child("weekEducationRatio").setValue(0);
                    personalRef.child("monthEducationRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthEntBudgetRatios() {
        Query query = budgetRef.orderByChild("item").equalTo("Entertainment");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));

                    }
                    int dayEntertainmentRatio = pTotal / 30;
                    int weekEntertainmentRatio = pTotal / 4;
                    int monthEntertainmentRatio = pTotal;

                    personalRef.child("dayEntertainmentRatio").setValue(dayEntertainmentRatio);
                    personalRef.child("weekEntertainmentRatio").setValue(weekEntertainmentRatio);
                    personalRef.child("monthEntertainmentRatio").setValue(monthEntertainmentRatio);
                } else {
                    personalRef.child("dayEntertainmentRatio").setValue(0);
                    personalRef.child("weekEntertainmentRatio").setValue(0);
                    personalRef.child("monthEntertainmentRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthHouseBudgetRatios() {
        Query query = budgetRef.orderByChild("item").equalTo("House");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));

                    }
                    int dayHouseRatio = pTotal / 30;
                    int weekHouseRatio = pTotal / 4;
                    int monthHouseRatio = pTotal;

                    personalRef.child("dayHouseRatio").setValue(dayHouseRatio);
                    personalRef.child("weekHouseRatio").setValue(weekHouseRatio);
                    personalRef.child("monthHouseRatio").setValue(monthHouseRatio);
                } else {
                    personalRef.child("dayHouseRatio").setValue(0);
                    personalRef.child("weekHouseRatio").setValue(0);
                    personalRef.child("monthHouseRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthFoodBudgetRatios() {
        Query query = budgetRef.orderByChild("item").equalTo("Food");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));

                    }
                    int dayFoodRatio = pTotal / 30;
                    int weekFoodRatio = pTotal / 4;
                    int monthFoodRatio = pTotal;

                    personalRef.child("dayFoodRatio").setValue(dayFoodRatio);
                    personalRef.child("weekFoodRatio").setValue(weekFoodRatio);
                    personalRef.child("monthFoodRatio").setValue(monthFoodRatio);
                } else {
                    personalRef.child("dayFoodRatio").setValue(0);
                    personalRef.child("weekFoodRatio").setValue(0);
                    personalRef.child("monthFoodRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthTransportBadget() {
        Query query = budgetRef.orderByChild("item").equalTo("Transport");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));

                    }
                    int dayTransRatio = pTotal / 30;
                    int weekTransRatio = pTotal / 4;
                    int monthTransRatio = pTotal;

                    personalRef.child("dayTransRatio").setValue(dayTransRatio);
                    personalRef.child("weekTransRatio").setValue(weekTransRatio);
                    personalRef.child("monthTransRatio").setValue(monthTransRatio);
                } else {
                    personalRef.child("dayTransRatio").setValue(0);
                    personalRef.child("weekTransRatio").setValue(0);
                    personalRef.child("monthTransRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clickListners() {
        budget_BTN_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });
    }

    private void addItem() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final Spinner itemSpinner = myView.findViewById(R.id.item_spinner);
        final EditText amount = myView.findViewById(R.id.input_EDT_amount);
        final MaterialButton cancel = myView.findViewById(R.id.input_BTN_cancel);
        final MaterialButton submit = myView.findViewById(R.id.input_BTN_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String budgetAmount = amount.getText().toString().trim();
                String budgetItem = itemSpinner.getSelectedItem().toString().trim();

                if (TextUtils.isEmpty(budgetAmount)) {
                    amount.setError("Amount is required");
                    amount.requestFocus();
                    return;
                }
                if (budgetItem.equals("Select item")) {
                    Toast.makeText(BudgetActivity.this, "Select a valid item", Toast.LENGTH_SHORT).show();
                } else {
                    loader.setMessage("adding a budget item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id = budgetRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar calendar = Calendar.getInstance();
                    String date = dateFormat.format(calendar.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Weeks weeks = Weeks.weeksBetween(epoch, now);
                    Months months = Months.monthsBetween(epoch, now);

                    String itemDay = budgetItem + date;
                    String itemWeeks = budgetItem + weeks.getWeeks();
                    String itemMonth = budgetItem + months.getMonths();

                    Data data = new Data(budgetItem, date, id, itemDay, itemWeeks, itemMonth, Integer.parseInt(budgetAmount), months.getMonths(), weeks.getWeeks(), null);

                    budgetRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(BudgetActivity.this, "Budget item was added successfuly", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });
                }
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>().setQuery(budgetRef, Data.class).build();
        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Data model) {
                holder.setItemAmount("Amount: $" + model.getAmount());
                holder.setDate("Date:" + model.getDate());
                holder.setItemsName("  " + model.getItem());
                holder.notes.setVisibility(View.GONE);

                switch (model.getItem()) {
                    case "Transport":
                        holder.imageView.setImageResource(R.drawable.ic_transport);
                        break;
                    case "Food":
                        holder.imageView.setImageResource(R.drawable.ic_food);
                        break;
                    case "House":
                        holder.imageView.setImageResource(R.drawable.ic_house);
                        break;
                    case "Entertainment":
                        holder.imageView.setImageResource(R.drawable.ic_entertainment);
                        break;
                    case "Education":
                        holder.imageView.setImageResource(R.drawable.ic_education);
                        break;
                    case "Charity":
                        holder.imageView.setImageResource(R.drawable.ic_charity);
                        break;
                    case "Apparel":
                        holder.imageView.setImageResource(R.drawable.ic_apparel);
                        break;
                    case "Health":
                        holder.imageView.setImageResource(R.drawable.ic_health);
                        break;
                    case "Personal":
                        holder.imageView.setImageResource(R.drawable.ic_personal);
                        break;
                    case "Other":
                        holder.imageView.setImageResource(R.drawable.ic_other);
                        break;
                }

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key = getRef(position).getKey();
                        item = model.getItem();
                        amount = model.getAmount();
                        updateData();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrive_layout, parent, false);
                return new MyViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();

    }

    private void updateData() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.update_layout, null);

        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();

        final MaterialTextView mItem = mView.findViewById(R.id.upddate_LBL_item);
        final EditText mAmount = mView.findViewById(R.id.update_EDT_amount);
        final EditText mNotes = mItem.findViewById(R.id.update_EDT_note);
        final MaterialTextView update_LBL_update = mView.findViewById(R.id.update_LBL_update);

        //update item and amount
        mItem.setText(item);
        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

//        //delete or update buttonns
        MaterialButton delB = mView.findViewById(R.id.update_BTN_delete);
        MaterialButton updatB = mView.findViewById(R.id.update_BTN_update);

        //update amount has been put
        updatB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                amount = Integer.parseInt(mAmount.getText().toString());

                //date
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar calendar = Calendar.getInstance();
                String date = dateFormat.format(calendar.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Weeks weeks = Weeks.weeksBetween(epoch, now);
                Months months = Months.monthsBetween(epoch, now);

                String itemDay = item + date;
                String itemWeeks = item + weeks.getWeeks();
                String itemMonth = item + months.getMonths();

                //data update
                Data data = new Data(item, date, post_key, itemDay, itemWeeks, itemMonth, amount, months.getMonths(), weeks.getWeeks(), null);

                budgetRef.child(post_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(BudgetActivity.this, "Budget item was updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        //delete item
        delB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //remove data from db
                budgetRef.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(BudgetActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public ImageView imageView;
        public TextView notes;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            imageView = itemView.findViewById(R.id.retrive_IMV_house);
            notes = itemView.findViewById(R.id.retrive_Note);
        }

        public void setItemsName(String itemsName) {
            TextView item = mView.findViewById(R.id.retrive_item);
            item.setText(itemsName);
        }

        public void setItemAmount(String itemAmount) {
            TextView amount = mView.findViewById(R.id.retrive_amount);
            amount.setText(itemAmount);

        }

        public void setDate(String itemDate) {
            TextView date = mView.findViewById(R.id.retrive_date);
            date.setText(itemDate);
        }
    }

    private void findViews() {
        budget_BTN_fab = findViewById(R.id.budget_BTN_fab);
        budget_LBL_totalBudgetAmount = findViewById(R.id.budget_LBL_totalBudgetAmount);
        recyclerView = findViewById(R.id.recyclerView);

    }


}