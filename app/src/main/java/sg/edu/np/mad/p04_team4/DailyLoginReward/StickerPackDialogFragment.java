package sg.edu.np.mad.p04_team4.DailyLoginReward;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.p04_team4.Chat.ChatDetailActivity;
import sg.edu.np.mad.p04_team4.Chat.StickerAdapter;
import sg.edu.np.mad.p04_team4.R;

public class StickerPackDialogFragment extends DialogFragment {

    private static final String TAG = "StickerPackDialog";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private RecyclerView stickerRecyclerView;
    private StickerAdapter stickerAdapter;
    private List<Uri> stickerPaths;
    private Button btnBuyStickerPack;
    private TextView coinAmountTextView;
    private LinearLayoutManager layoutManager;

    private String packName;
    private String userId;
    private String chatRoomId;
    private int packCost;
    private int currentCoins;
    private boolean isUserView;

    // Constructor to initialize the sticker pack dialog with necessary information
    public StickerPackDialogFragment(String packName, String userId, String chatRoomId, int packCost, boolean isUserView) {
        this.packName = packName;
        this.userId = userId;
        this.chatRoomId = chatRoomId;
        this.packCost = packCost;
        this.isUserView = isUserView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for the sticker pack dialog
        View view = inflater.inflate(R.layout.dialog_sticker_pack, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        stickerRecyclerView = view.findViewById(R.id.stickerRecyclerView);
        TextView packNameTextView = view.findViewById(R.id.stickerPackTitle);
        ImageView backArrow = view.findViewById(R.id.backArrow);
        btnBuyStickerPack = view.findViewById(R.id.btnBuyStickerPack);
        coinAmountTextView = getActivity().findViewById(R.id.coinAmount);

        packNameTextView.setText(packName);

        // Set a click listener to dismiss the dialog when the back arrow is clicked
        backArrow.setOnClickListener(v -> dismiss());

        stickerPaths = new ArrayList<>();
        // Initialize the sticker adapter with a click listener to send stickers
        stickerAdapter = new StickerAdapter(stickerPaths, getContext(), url -> {
            sendStickerMessage(url.toString());
            dismiss();
        }, true);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        stickerRecyclerView.setLayoutManager(layoutManager);
        stickerRecyclerView.setAdapter(stickerAdapter);

        // Fetch user coins and check if the sticker pack is purchased
        fetchUserCoins();
        checkIfStickerPackPurchased();

        // Set a click listener to show a purchase confirmation dialog
        btnBuyStickerPack.setOnClickListener(v -> showPurchaseConfirmationDialog());

        return view;
    }

    // Fetch the current coin balance of the user from the database
    private void fetchUserCoins() {
        DatabaseReference userCoinsRef = mDatabase.child("users").child(userId).child("friendCoins");
        userCoinsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer coins = dataSnapshot.getValue(Integer.class);
                if (coins != null) {
                    currentCoins = coins;
                } else {
                    currentCoins = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to fetch coins", databaseError.toException());
            }
        });
    }

    // Check if the sticker pack is already purchased by the user
    private void checkIfStickerPackPurchased() {
        DatabaseReference userPacksRef = mDatabase.child("users").child(userId).child("purchasedStickerPacks").child(packName);
        userPacksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isPurchased = dataSnapshot.exists();
                if (isPurchased) {
                    Log.d(TAG, "Sticker pack " + packName + " is already purchased.");
                    loadStickersFromPack();
                    btnBuyStickerPack.setVisibility(View.GONE); // Hide the buy button if already purchased
                } else {
                    Log.d(TAG, "Sticker pack " + packName + " is not purchased.");
                    loadStickersFromPack();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to check sticker pack purchase status", databaseError.toException());
            }
        });
    }

    // Load stickers based on the selected pack name
    private void loadStickersFromPack() {
        switch (packName) {
            case "Cat Sticker Pack":
                loadCatStickers();
                break;
            case "Dead by Daylight Sticker Pack":
                loadDeadByDaylightStickers();
                break;
            case "Alpha Wolf Sticker Pack":
                loadAlphaWolfStickers();
                break;
            case "Monkey Sticker Pack":
                loadMonkeyStickers();
                break;
            case "Skibidi Toilet Sticker Pack":
                loadSkibidiStickers();
                break;
            case "Emoji Sticker Pack":
                loadEmojiStickers();
                break;
        }
    }

    // Load Cat stickers into the recycler view
    private void loadCatStickers() {
        stickerPaths.add(getResourceUri(R.drawable.cat_angry));
        stickerPaths.add(getResourceUri(R.drawable.cat_bored));
        stickerPaths.add(getResourceUri(R.drawable.cat_crying));
        stickerPaths.add(getResourceUri(R.drawable.cat_facepalm));
        stickerPaths.add(getResourceUri(R.drawable.cat_happy));
        stickerPaths.add(getResourceUri(R.drawable.cat_sleep));
        stickerAdapter.notifyDataSetChanged();
    }

    // Load Dead by Daylight stickers into the recycler view
    private void loadDeadByDaylightStickers() {
        stickerPaths.add(getResourceUri(R.drawable.claudette));
        stickerPaths.add(getResourceUri(R.drawable.dwight));
        stickerPaths.add(getResourceUri(R.drawable.ghostface));
        stickerPaths.add(getResourceUri(R.drawable.legion));
        stickerPaths.add(getResourceUri(R.drawable.sadako));
        stickerAdapter.notifyDataSetChanged();
    }

    // Load Alpha Wolf stickers into the recycler view
    private void loadAlphaWolfStickers() {
        stickerPaths.add(getResourceUri(R.drawable.wolf_grin));
        stickerPaths.add(getResourceUri(R.drawable.wolf_growl));
        stickerPaths.add(getResourceUri(R.drawable.wolf_howl));
        stickerPaths.add(getResourceUri(R.drawable.wolf_pack));
        stickerAdapter.notifyDataSetChanged();
    }

    // Load Monkey stickers into the recycler view
    private void loadMonkeyStickers() {
        stickerPaths.add(getResourceUri(R.drawable.monkey_angry));
        stickerPaths.add(getResourceUri(R.drawable.monkey_fp));
        stickerPaths.add(getResourceUri(R.drawable.monkey_happy));
        stickerPaths.add(getResourceUri(R.drawable.monkey_no));
        stickerPaths.add(getResourceUri(R.drawable.monkey_yes));
        stickerPaths.add(getResourceUri(R.drawable.monkey_sad));
        stickerAdapter.notifyDataSetChanged();
    }

    // Load Skibidi Toilet stickers into the recycler view
    private void loadSkibidiStickers() {
        stickerPaths.add(getResourceUri(R.drawable.skibidi_angry));
        stickerPaths.add(getResourceUri(R.drawable.skibidi_confused));
        stickerPaths.add(getResourceUri(R.drawable.skibidi_happy));
        stickerPaths.add(getResourceUri(R.drawable.skibidi_mindblown));
        stickerPaths.add(getResourceUri(R.drawable.skibidi_sad));
        stickerAdapter.notifyDataSetChanged();
    }

    // Load Emoji stickers into the recycler view
    private void loadEmojiStickers() {
        stickerPaths.add(getResourceUri(R.drawable.emoji_angry));
        stickerPaths.add(getResourceUri(R.drawable.emoji_cry));
        stickerPaths.add(getResourceUri(R.drawable.emoji_laugh));
        stickerPaths.add(getResourceUri(R.drawable.emoji_mock));
        stickerAdapter.notifyDataSetChanged();
    }

    // Get the URI for a drawable resource
    private Uri getResourceUri(int resourceId) {
        return Uri.parse("android.resource://" + getContext().getPackageName() + "/" + resourceId);
    }

    // Show a confirmation dialog before purchasing the sticker pack
    private void showPurchaseConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.make_purchase))
                .setMessage(getString(R.string.buy_sticker1) + " " + packCost + " " + getString(R.string.buy_sticker2))
                .setPositiveButton(getString(R.string.buy_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        buyStickerPack();
                    }
                })
                .setNegativeButton(getString(R.string.buy_no), null)
                .show();
    }

    // Handle the sticker pack purchase process
    private void buyStickerPack() {
        DatabaseReference userCoinsRef = mDatabase.child("users").child(userId).child("friendCoins");
        userCoinsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer currentCoins = dataSnapshot.getValue(Integer.class);
                if (currentCoins != null && currentCoins >= packCost) {
                    int newBalance = currentCoins - packCost;
                    userCoinsRef.setValue(newBalance).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            savePurchasedStickerPack();
                            ((ShopActivity) getActivity()).updateCoinDisplay(newBalance); // Update coin display
                            Toast.makeText(getContext(), getString(R.string.sticker_purchased), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Failed to update coins", task.getException());
                            Toast.makeText(getContext(), getString(R.string.purchase_failed), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), getString(R.string.not_enough_coins), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to fetch coins", databaseError.toException());
            }
        });
    }

    // Save the purchased sticker pack to the user's account
    private void savePurchasedStickerPack() {
        DatabaseReference userPacksRef = mDatabase.child("users").child(userId).child("purchasedStickerPacks").child(packName);
        userPacksRef.setValue(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Sticker pack saved to user's purchased packs");
                btnBuyStickerPack.setVisibility(View.GONE);
                loadStickersFromPack(); // Display stickers after purchase
            } else {
                Log.e(TAG, "Failed to save sticker pack", task.getException());
            }
        });
    }

    // Send a sticker message in the chat
    private void sendStickerMessage(String stickerPath) {
        ChatDetailActivity chatDetailActivity = (ChatDetailActivity) getActivity();
        if (chatDetailActivity != null) {
            chatDetailActivity.sendStickerMessage(stickerPath);
        }
    }
}
