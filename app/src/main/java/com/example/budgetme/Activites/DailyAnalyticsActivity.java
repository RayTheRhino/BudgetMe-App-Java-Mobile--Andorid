package com.example.budgetme.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.example.budgetme.R;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class DailyAnalyticsActivity extends AppCompatActivity {

    private MaterialTextView analystic_LBL_totAmount;
    private RelativeLayout linearLayoutAnalysis, relativLayout1, relativLayout2, relativLayout3, relativLayout4, relativLayout5, relativLayout6, relativLayout7,
            relativLayout8, relativLayout9, relativLayout10;
    private TextView relativeLayout_LBL_amount, relativeLayout_LBL_amount1, relativeLayout_LBL_amount2, relativeLayout_LBL_amount3, relativeLayout_LBL_amount4,
            relativeLayout_LBL_amount5, relativeLayout_LBL_amount6, relativeLayout_LBL_amount7, relativeLayout_LBL_amount8, relativeLayout_LBL_amount9, dailySpentAmount,
            dailyRealationSpending, transp_LBL_percent, food_LBL_percent, house_LBL_percent, enter_LBL_percent, educ_LBL_percent, chaira_LBL_percent,
            appe_LBL_percent, heal_LBL_percent, perso_LBL_precent, oth_LBL_percent;
    private ImageView transport_LVL_status, food_LVL_status, house_LVL_status, entertainment_LVL_status, education_LVL_status, charity_LVL_status, apparel_LVL_status,
            health_LVL_status, personal_LVL_status, other_LVL_status, dilyRealationSpending_IMG, blue_IMG_status, pink_IMG_status, red_IMG_status;
    private AnyChartView anyChartsView;
    //firebase
    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef, personalRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_analytics);
        findViews();

        //firebase:
        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(onlineUserId);

        getWeeklyTransportBadget();
        getWeeklyFoodBudgetRatios(); // there is a bug doesnt show on the daily analystic
        getWeeklyHouseBudgetRatios();
        getWeeklyEntBudgetRatios(); //Entertainment
        getWeeklyEduRatios(); //Education
        getWeeklyChairatyRatios();
        getWeeklyAppRatios(); //apparel
        getWeeklyHealthRatios();
        getWeeklyPerRatios(); //personal
        getWeeklyOtherRatios();
        getTotalDaySpending();
        setStatusAndImageResurce();
        loadGraph();

    }

    private void getTotalDaySpending() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        dailySpentAmount.setText("$" + totalAmount);
                        analystic_LBL_totAmount.setText("Today you spent " + "$" + totalAmount);
                    }
                    personalRef.child("daySpent").setValue(totalAmount);
                } else {
                    linearLayoutAnalysis.setVisibility(View.GONE);
                    dailySpentAmount.setText("you have not spent today");
                    anyChartsView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getWeeklyOtherRatios() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemDay = "Other" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemDay").equalTo(itemDay);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        relativeLayout_LBL_amount9.setText("$" + totalAmount);
                    }
                    personalRef.child("dayOther").setValue(totalAmount);
                } else {
                    relativLayout10.setVisibility(View.GONE);
                    personalRef.child("dayOther").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getWeeklyPerRatios() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemDay = "Personal" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemDay").equalTo(itemDay);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        relativeLayout_LBL_amount8.setText("$" + totalAmount);
                    }
                    personalRef.child("dayPersonal").setValue(totalAmount);
                } else {
                    relativLayout9.setVisibility(View.GONE);
                    personalRef.child("dayPersonal").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getWeeklyHealthRatios() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemDay = "Health" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemDay").equalTo(itemDay);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        relativeLayout_LBL_amount7.setText("$" + totalAmount);
                    }
                    personalRef.child("dayHealth").setValue(totalAmount);
                } else {
                    relativLayout8.setVisibility(View.GONE);
                    personalRef.child("dayHealth").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getWeeklyAppRatios() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemDay = "Apparel" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemDay").equalTo(itemDay);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        relativeLayout_LBL_amount6.setText("$" + totalAmount);
                    }
                    personalRef.child("dayApparel").setValue(totalAmount);
                } else {
                    relativLayout7.setVisibility(View.GONE);
                    personalRef.child("dayApparel").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getWeeklyChairatyRatios() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemDay = "Charity" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemDay").equalTo(itemDay);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        relativeLayout_LBL_amount5.setText("$" + totalAmount);
                    }
                    personalRef.child("dayCharity").setValue(totalAmount);
                } else {
                    relativLayout6.setVisibility(View.GONE);
                    personalRef.child("dayCharity").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getWeeklyEduRatios() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemDay = "Education" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemDay").equalTo(itemDay);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        relativeLayout_LBL_amount4.setText("$" + totalAmount);
                    }
                    personalRef.child("dayEducation").setValue(totalAmount);
                } else {
                    relativLayout5.setVisibility(View.GONE);
                    personalRef.child("dayEducation").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getWeeklyEntBudgetRatios() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemDay = "Entertainment" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemDay").equalTo(itemDay);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        relativeLayout_LBL_amount3.setText("$" + totalAmount);
                    }
                    personalRef.child("dayEntertainment").setValue(totalAmount);
                } else {
                    relativLayout4.setVisibility(View.GONE);
                    personalRef.child("dayEntertainment").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getWeeklyHouseBudgetRatios() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemDay = "House" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemDay").equalTo(itemDay);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        relativeLayout_LBL_amount2.setText("$" + totalAmount);
                    }
                    personalRef.child("dayHouse").setValue(totalAmount);
                } else {
                    relativLayout3.setVisibility(View.GONE);
                    personalRef.child("dayHouse").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getWeeklyFoodBudgetRatios() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemDay = "Food" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemDay").equalTo(itemDay);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        relativeLayout_LBL_amount1.setText("$" + totalAmount);
                    }
                    personalRef.child("dayFood").setValue(totalAmount);
                } else {
                    relativLayout2.setVisibility(View.GONE);
                    personalRef.child("dayFood").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getWeeklyTransportBadget() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemDay = "Transport" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemDay").equalTo(itemDay);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        relativeLayout_LBL_amount.setText("$" + totalAmount);
                    }
                    personalRef.child("dayTrans").setValue(totalAmount);
                } else {
                    relativLayout1.setVisibility(View.GONE);
                    personalRef.child("dayTrans").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadGraph() {
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int traTotal;
                    if (snapshot.hasChild("dayTrans")) { //transport
                        traTotal = Integer.parseInt(snapshot.child("dayTrans").getValue().toString());
                    } else {
                        traTotal = 0;
                    }
                    int foodTotal;
                    if (snapshot.hasChild("dayFood")) { //food
                        foodTotal = Integer.parseInt(snapshot.child("dayFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }
                    int houseTotal;
                    if (snapshot.hasChild("dayHouse")) { //house
                        houseTotal = Integer.parseInt(snapshot.child("dayHouse").getValue().toString());
                    } else {
                        houseTotal = 0;
                    }
                    int entTotal;
                    if (snapshot.hasChild("dayEntertainment")) { //Entertainment
                        entTotal = Integer.parseInt(snapshot.child("dayEntertainment").getValue().toString());
                    } else {
                        entTotal = 0;
                    }
                    int eduTotal;
                    if (snapshot.hasChild("dayEducation")) { //Education
                        eduTotal = Integer.parseInt(snapshot.child("dayEducation").getValue().toString());
                    } else {
                        eduTotal = 0;
                    }
                    int chairTotal;
                    if (snapshot.hasChild("dayCharity")) { //Charity
                        chairTotal = Integer.parseInt(snapshot.child("dayCharity").getValue().toString());
                    } else {
                        chairTotal = 0;
                    }
                    int appTotal;
                    if (snapshot.hasChild("dayApparel")) { //Apparel
                        appTotal = Integer.parseInt(snapshot.child("dayApparel").getValue().toString());
                    } else {
                        appTotal = 0;
                    }
                    int healthTotal;
                    if (snapshot.hasChild("dayHealth")) { //Health
                        healthTotal = Integer.parseInt(snapshot.child("dayHealth").getValue().toString());
                    } else {
                        healthTotal = 0;
                    }
                    int perTotal;
                    if (snapshot.hasChild("dayPersonal")) { //Personal
                        perTotal = Integer.parseInt(snapshot.child("dayPersonal").getValue().toString());
                    } else {
                        perTotal = 0;
                    }
                    int otherTotal;
                    if (snapshot.hasChild("dayOther")) { //other
                        otherTotal = Integer.parseInt(snapshot.child("dayOther").getValue().toString());
                    } else {
                        otherTotal = 0;
                    }

                    Pie pie = AnyChart.pie();
                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("Transport", traTotal));
                    data.add(new ValueDataEntry("House", houseTotal));
                    data.add(new ValueDataEntry("Food", foodTotal));
                    data.add(new ValueDataEntry("Entertainment", entTotal));
                    data.add(new ValueDataEntry("Education", eduTotal));
                    data.add(new ValueDataEntry("Charity", chairTotal));
                    data.add(new ValueDataEntry("Apparel", appTotal));
                    data.add(new ValueDataEntry("Health", healthTotal));
                    data.add(new ValueDataEntry("Personal", perTotal));
                    data.add(new ValueDataEntry("Other", otherTotal));

                    pie.data(data);
                    pie.title("Daily Analytic: ");
                    pie.labels().position("outside");
                    pie.legend().title().enabled(true);
                    pie.legend().title().text("Items spent on: ").padding(0d, 0d, 10d, 0d);

                    pie.legend().position("center-bottom").itemsLayout(LegendLayout.HORIZONTAL).align(Align.CENTER);
                    anyChartsView.setChart(pie);
                } else {
                    Toast.makeText(DailyAnalyticsActivity.this, "Child does not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setStatusAndImageResurce() {
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    float traTotal;
                    if (snapshot.hasChild("dayTrans")) { //transport
                        traTotal = Integer.parseInt(snapshot.child("dayTrans").getValue().toString());
                    } else {
                        traTotal = 0;
                    }
                    float foodTotal;
                    if (snapshot.hasChild("dayFood")) { //food
                        foodTotal = Integer.parseInt(snapshot.child("dayFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }
                    float houseTotal;
                    if (snapshot.hasChild("dayHouse")) { //house
                        houseTotal = Integer.parseInt(snapshot.child("dayHouse").getValue().toString());
                    } else {
                        houseTotal = 0;
                    }
                    float entTotal;
                    if (snapshot.hasChild("dayEntertainment")) { //Entertainment
                        entTotal = Integer.parseInt(snapshot.child("dayEntertainment").getValue().toString());
                    } else {
                        entTotal = 0;
                    }
                    float eduTotal;
                    if (snapshot.hasChild("dayEducation")) { //Education
                        eduTotal = Integer.parseInt(snapshot.child("dayEducation").getValue().toString());
                    } else {
                        eduTotal = 0;
                    }
                    float chairTotal;
                    if (snapshot.hasChild("dayCharity")) { //Charity
                        chairTotal = Integer.parseInt(snapshot.child("dayCharity").getValue().toString());
                    } else {
                        chairTotal = 0;
                    }
                    float appTotal;
                    if (snapshot.hasChild("dayApparel")) { //Apparel
                        appTotal = Integer.parseInt(snapshot.child("dayApparel").getValue().toString());
                    } else {
                        appTotal = 0;
                    }
                    float healthTotal;
                    if (snapshot.hasChild("dayHealth")) { //Health
                        healthTotal = Integer.parseInt(snapshot.child("dayHealth").getValue().toString());
                    } else {
                        healthTotal = 0;
                    }
                    float perTotal;
                    if (snapshot.hasChild("dayPersonal")) { //Personal
                        perTotal = Integer.parseInt(snapshot.child("dayPersonal").getValue().toString());
                    } else {
                        perTotal = 0;
                    }
                    float otherTotal;
                    if (snapshot.hasChild("dayOther")) { //other
                        otherTotal = Integer.parseInt(snapshot.child("dayOther").getValue().toString());
                    } else {
                        otherTotal = 0;
                    }
                    float dayTotalSpentAmount;
                    if (snapshot.hasChild("day")) {
                        dayTotalSpentAmount = Integer.parseInt(snapshot.child("day").getValue().toString());
                    } else {
                        dayTotalSpentAmount = 0;
                    }

                    //Getting Ratio
                    float traRatio;
                    if (snapshot.hasChild("dayTransRatio")) {
                        traRatio = Integer.parseInt(snapshot.child("dayTransRatio").getValue().toString());
                    } else {
                        traRatio = 0;
                    }
                    float foodRatio; //Food
                    if (snapshot.hasChild("dayFoodRatio")) {
                        foodRatio = Integer.parseInt(snapshot.child("dayFoodRatio").getValue().toString());
                    } else {
                        foodRatio = 0;
                    }
                    float houseRatio; //House
                    if (snapshot.hasChild("dayHouseRatio")) {
                        houseRatio = Integer.parseInt(snapshot.child("dayHouseRatio").getValue().toString());
                    } else {
                        houseRatio = 0;
                    }
                    float entreRatio; //Entertainment
                    if (snapshot.hasChild("dayEntertainmentRatio")) {
                        entreRatio = Integer.parseInt(snapshot.child("dayEntertainmentRatio").getValue().toString());
                    } else {
                        entreRatio = 0;
                    }
                    float eduRatio; //Education
                    if (snapshot.hasChild("dayEducationRatio")) {
                        eduRatio = Integer.parseInt(snapshot.child("dayEducationRatio").getValue().toString());
                    } else {
                        eduRatio = 0;
                    }
                    float chairRatio; //Charity
                    if (snapshot.hasChild("dayCharityRatio")) {
                        chairRatio = Integer.parseInt(snapshot.child("dayCharityRatio").getValue().toString());
                    } else {
                        chairRatio = 0;
                    }
                    float appRatio; //Apparel
                    if (snapshot.hasChild("dayApparelRatio")) {
                        appRatio = Integer.parseInt(snapshot.child("dayApparelRatio").getValue().toString());
                    } else {
                        appRatio = 0;
                    }
                    float healthRatio; //Health
                    if (snapshot.hasChild("dayHealthRatio")) {
                        healthRatio = Integer.parseInt(snapshot.child("dayHealthRatio").getValue().toString());
                    } else {
                        healthRatio = 0;
                    }
                    float persRatio; //Personal
                    if (snapshot.hasChild("dayPersonalRatio")) {
                        persRatio = Integer.parseInt(snapshot.child("dayPersonalRatio").getValue().toString());
                    } else {
                        persRatio = 0;
                    }
                    float otherRatio; //other
                    if (snapshot.hasChild("dayOtherRatio")) {
                        otherRatio = Integer.parseInt(snapshot.child("dayOtherRatio").getValue().toString());
                    } else {
                        otherRatio = 0;
                    }
                    float dayTotalSpentAmountRatio;
                    if (snapshot.hasChild("dailyBudget")) {
                        dayTotalSpentAmountRatio = Integer.parseInt(snapshot.child("dailyBudget").getValue().toString());
                    } else {
                        dayTotalSpentAmountRatio = 0;
                    }
                    //what kind of dot to put
                    float monthPrecent = (dayTotalSpentAmount / dayTotalSpentAmountRatio) * 100;
                    if (monthPrecent < 50) {
                        dailyRealationSpending.setText("Less then 50%");
                        dilyRealationSpending_IMG.setImageResource(R.drawable.ic_blue);
                    } else if (monthPrecent >= 50 && monthPrecent < 100) {
                        dailyRealationSpending.setText(" between 50%-90%");
                        dilyRealationSpending_IMG.setImageResource(R.drawable.ic_pink);
                    } else {
                        dailyRealationSpending.setText("Above 100%");
                        dilyRealationSpending_IMG.setImageResource(R.drawable.ic_red);
                    }

                    float transPrecent = (traTotal / traRatio) * 100; //transport
                    if (transPrecent < 50) {
                        transp_LBL_percent.setText("Less then 50%");
                        transport_LVL_status.setImageResource(R.drawable.ic_blue);
                    } else if (transPrecent >= 50 && transPrecent < 100) {
                        transp_LBL_percent.setText(" between 50%-90%");
                        transport_LVL_status.setImageResource(R.drawable.ic_pink);
                    } else {
                        transp_LBL_percent.setText("Above 100%" );
                        transport_LVL_status.setImageResource(R.drawable.ic_red);
                    }

                    float foodPrecent = (foodTotal / foodRatio) * 100; //food
                    if (foodPrecent < 50) {
                        food_LBL_percent.setText("Less then 50%" );
                        food_LVL_status.setImageResource(R.drawable.ic_blue);
                    } else if (foodPrecent >= 50 && foodPrecent < 100) {
                        food_LBL_percent.setText(" between 50%-90%");
                        food_LVL_status.setImageResource(R.drawable.ic_pink);
                    } else {
                        food_LBL_percent.setText("Above 100%");
                        food_LVL_status.setImageResource(R.drawable.ic_red);
                    }

                    float housePrecent = (houseTotal / houseRatio) * 100;//house
                    if (housePrecent < 50) {
                        house_LBL_percent.setText("Less then 50%");
                        house_LVL_status.setImageResource(R.drawable.ic_blue);
                    } else if (housePrecent >= 50 && housePrecent < 100) {
                        house_LBL_percent.setText(" between 50%-90%");
                        house_LVL_status.setImageResource(R.drawable.ic_pink);
                    } else {
                        house_LBL_percent.setText("Above 100%");
                        house_LVL_status.setImageResource(R.drawable.ic_red);
                    }

                    float enterPrecent = (entTotal / entreRatio) * 100; //entertaiment
                    if (enterPrecent < 50) {
                        enter_LBL_percent.setText("Less then 50%");
                        entertainment_LVL_status.setImageResource(R.drawable.ic_blue);
                    } else if (enterPrecent >= 50 && enterPrecent < 100) {
                        enter_LBL_percent.setText(" between 50%-90%");
                        entertainment_LVL_status.setImageResource(R.drawable.ic_pink);
                    } else {
                        enter_LBL_percent.setText("Above 100%" );
                        entertainment_LVL_status.setImageResource(R.drawable.ic_red);
                    }

                    float eduPrecent = (eduTotal / eduRatio) * 100;//education
                    if (eduPrecent < 50) {
                        educ_LBL_percent.setText("Less then 50%");
                        education_LVL_status.setImageResource(R.drawable.ic_blue);
                    } else if (eduPrecent >= 50 && eduPrecent < 100) {
                        educ_LBL_percent.setText(" between 50%-90%");
                        education_LVL_status.setImageResource(R.drawable.ic_pink);
                    } else {
                        educ_LBL_percent.setText("Above 100%");
                        education_LVL_status.setImageResource(R.drawable.ic_red);
                    }

                    float chairPrecent = (chairTotal / chairRatio) * 100; //chairty
                    if (chairPrecent < 50) {
                        chaira_LBL_percent.setText("Less then 50%");
                        charity_LVL_status.setImageResource(R.drawable.ic_blue);
                    } else if (chairPrecent >= 50 && chairPrecent < 100) {
                        chaira_LBL_percent.setText(" between 50%-90%" );
                        charity_LVL_status.setImageResource(R.drawable.ic_pink);
                    } else {
                        chaira_LBL_percent.setText("Above 100%");
                        charity_LVL_status.setImageResource(R.drawable.ic_red);
                    }

                    float appPrecent = (appTotal / appRatio) * 100;//apparel
                    if (appPrecent < 50) {
                        appe_LBL_percent.setText("Less then 50%");
                        apparel_LVL_status.setImageResource(R.drawable.ic_blue);
                    } else if (appPrecent >= 50 && appPrecent < 100) {
                        appe_LBL_percent.setText(" between 50%-90%");
                        apparel_LVL_status.setImageResource(R.drawable.ic_pink);
                    } else {
                        appe_LBL_percent.setText("Above 100%");
                        apparel_LVL_status.setImageResource(R.drawable.ic_red);
                    }

                    float healthPrecent = (healthTotal / healthRatio) * 100;//health
                    if (healthPrecent < 50) {
                        heal_LBL_percent.setText("Less then 50%");
                        health_LVL_status.setImageResource(R.drawable.ic_blue);
                    } else if (healthPrecent >= 50 && healthPrecent < 100) {
                        heal_LBL_percent.setText(" between 50%-90%");
                        health_LVL_status.setImageResource(R.drawable.ic_pink);
                    } else {
                        heal_LBL_percent.setText("Above 100%");
                        health_LVL_status.setImageResource(R.drawable.ic_red);
                    }

                    float persoPrecent = (perTotal / persRatio) * 100; //personal
                    if (persoPrecent < 50) {
                        perso_LBL_precent.setText("Less then 50%");
                        personal_LVL_status.setImageResource(R.drawable.ic_blue);
                    } else if (persoPrecent >= 50 && persoPrecent < 100) {
                        perso_LBL_precent.setText(" between 50%-90%");
                        personal_LVL_status.setImageResource(R.drawable.ic_pink);
                    } else {
                        perso_LBL_precent.setText("Above 100%");
                        personal_LVL_status.setImageResource(R.drawable.ic_red);
                    }

                    float otherPrecent = (otherTotal / otherRatio) * 100; //other
                    if (otherPrecent < 50) {
                        oth_LBL_percent.setText("Less then 50%");
                        other_LVL_status.setImageResource(R.drawable.ic_blue);
                    } else if (otherPrecent >= 50 && otherPrecent < 100) {
                        oth_LBL_percent.setText(" between 50%-90%");
                        other_LVL_status.setImageResource(R.drawable.ic_pink);
                    } else {
                        oth_LBL_percent.setText("Above 100%");
                        other_LVL_status.setImageResource(R.drawable.ic_red);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void findViews() {
        //Relativelayouts
        linearLayoutAnalysis = findViewById(R.id.linearLayoutAnalysis);
        relativLayout1 = findViewById(R.id.relativLayout1);
        relativLayout2 = findViewById(R.id.relativLayout2);
        relativLayout3 = findViewById(R.id.relativLayout3);
        relativLayout4 = findViewById(R.id.relativLayout4);
        relativLayout5 = findViewById(R.id.relativLayout5);
        relativLayout6 = findViewById(R.id.relativLayout6);
        relativLayout7 = findViewById(R.id.relativLayout7);
        relativLayout8 = findViewById(R.id.relativLayout8);
        relativLayout9 = findViewById(R.id.relativLayout9);
        relativLayout10 = findViewById(R.id.relativLayout10);
        //TextViews
        dailyRealationSpending = findViewById(R.id.dailyRealationSpending);
        food_LBL_percent = findViewById(R.id.food_LBL_percent);
        transp_LBL_percent = findViewById(R.id.transp_LBL_percent);
        house_LBL_percent = findViewById(R.id.house_LBL_percent);
        enter_LBL_percent = findViewById(R.id.enter_LBL_percent);
        educ_LBL_percent = findViewById(R.id.educ_LBL_percent);
        chaira_LBL_percent = findViewById(R.id.chaira_LBL_percent);
        appe_LBL_percent = findViewById(R.id.appe_LBL_percent);
        heal_LBL_percent = findViewById(R.id.heal_LBL_percent);
        perso_LBL_precent = findViewById(R.id.perso_LBL_precent);
        oth_LBL_percent = findViewById(R.id.oth_LBL_percent);
        analystic_LBL_totAmount = findViewById(R.id.analystic_LBL_totAmount);
        relativeLayout_LBL_amount = findViewById(R.id.relativeLayout_LBL_amount);
        relativeLayout_LBL_amount1 = findViewById(R.id.relativeLayout_LBL_amount1);
        relativeLayout_LBL_amount2 = findViewById(R.id.relativeLayout_LBL_amount2);
        relativeLayout_LBL_amount3 = findViewById(R.id.relativeLayout_LBL_amount3);
        relativeLayout_LBL_amount4 = findViewById(R.id.relativeLayout_LBL_amount4);
        relativeLayout_LBL_amount5 = findViewById(R.id.relativeLayout_LBL_amount5);
        relativeLayout_LBL_amount6 = findViewById(R.id.relativeLayout_LBL_amount6);
        relativeLayout_LBL_amount7 = findViewById(R.id.relativeLayout_LBL_amount7);
        relativeLayout_LBL_amount8 = findViewById(R.id.relativeLayout_LBL_amount8);
        relativeLayout_LBL_amount9 = findViewById(R.id.relativeLayout_LBL_amount9);
        dailySpentAmount = findViewById(R.id.dailySpentAmount);
        dailyRealationSpending = findViewById(R.id.dailyRealationSpending);
        //Image Views
        transport_LVL_status = findViewById(R.id.transport_LVL_status);
        food_LVL_status = findViewById(R.id.food_LVL_status);
        house_LVL_status = findViewById(R.id.house_LVL_status);
        entertainment_LVL_status = findViewById(R.id.entertainment_LVL_status);
        education_LVL_status = findViewById(R.id.education_LVL_status);
        charity_LVL_status = findViewById(R.id.charity_LVL_status);
        apparel_LVL_status = findViewById(R.id.apparel_LVL_status);
        other_LVL_status = findViewById(R.id.other_LVL_status);
        health_LVL_status = findViewById(R.id.health_LVL_status);
        personal_LVL_status = findViewById(R.id.personal_LVL_status);
        dilyRealationSpending_IMG = findViewById(R.id.dilyRealationSpending_IMG);
        //colors Imgview
        blue_IMG_status = findViewById(R.id.blue_IMG_status);
        pink_IMG_status = findViewById(R.id.pink_IMG_status);
        red_IMG_status = findViewById(R.id.red_IMG_status);
        //anyChart View
        anyChartsView = findViewById(R.id.anyChartsView);

    }
}