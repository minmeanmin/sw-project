package com.example.naengjanggo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class FoodListActivity extends AppCompatActivity {
    private CardView selectedCard = null;
    private String selectedProduct = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize navigation buttons
        ImageButton backButton = findViewById(R.id.backButton);

        // Initialize grid items
        CardView kimchiCard = findViewById(R.id.kimchiCard);
        CardView jikobaCard = findViewById(R.id.jikobaCard);
        CardView eggsCard = findViewById(R.id.eggsCard);
        CardView milkCard = findViewById(R.id.milkCard);

        // Initialize next button
        Button nextButton = findViewById(R.id.nextButton);

        // Add click listeners
        backButton.setOnClickListener(v -> onBackPressed());

        View.OnClickListener cardClickListener = v -> {
            if (selectedCard != null) {
                selectedCard.setCardBackgroundColor(getResources().getColor(R.color.white));
            }
            selectedCard = (CardView) v;
            selectedCard.setCardBackgroundColor(getResources().getColor(R.color.selected_color));
            selectedProduct = getProductName(selectedCard);

        };

        kimchiCard.setOnClickListener(cardClickListener);
        jikobaCard.setOnClickListener(cardClickListener);
        eggsCard.setOnClickListener(cardClickListener);
        milkCard.setOnClickListener(cardClickListener);

        nextButton.setOnClickListener(v -> {
            if (selectedProduct != null) {
                Log.d("FoodListActivity", "Selected Product: " + selectedProduct);
                Intent intent = new Intent(FoodListActivity.this, FoodDetailActivity.class);
                intent.putExtra("PRODUCT_NAME", selectedProduct);
                startActivity(intent);
            } else {
                Toast.makeText(FoodListActivity.this, "제품을 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getProductName(CardView cardView) {
        int id = cardView.getId();
        if (id == R.id.kimchiCard) return "김치";
        if (id == R.id.jikobaCard) return "지코바";
        if (id == R.id.eggsCard) return "계란";
        if (id == R.id.milkCard) return "우유";
        return "";
    }
}