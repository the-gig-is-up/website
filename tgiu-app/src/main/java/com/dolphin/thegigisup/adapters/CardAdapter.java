package com.dolphin.thegigisup.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.IconicsTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dolphin.thegigisup.models.Card;
import com.dolphin.thegigisup.R;

import java.util.List;

/**
 * Adapter to assign card information to a RecyclerView
 *
 * @author Team Dolphin 13/04/15.
 */
public class CardAdapter
        extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    // Initialise the appropriate variables
    private final int layoutResource;
    private List<Card> cards;
    private int selectedItem = -1;
    private RecyclerView recyclerView;
    private OnItemClickListener onItemClickListener;

    /**
     * On attached to recyclerView, set the current RecyclerView
     */
    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    /**
     * Constructor that uses a list of cards and a layout resource
     *
     * @param cards A list of card objects
     * @param layoutResource An integer for a layout resource
     */
    public CardAdapter(List<Card> cards, int layoutResource) {
        this.cards = cards;
        this.layoutResource = layoutResource;
    }

    /**
     * Set the list of cards to a new list of cards and notify the data set
     *
     * @param cards A list of card objects
     */
    public void setCards(List<Card> cards) {
        if (cards != null) {
            this.cards = cards;
            notifyDataSetChanged();
            notifyItemRangeInserted(0, cards.size());
        }
        if (cards == null) {
            this.cards = null;
        }
    }

    /**
     * Add a card to the current card list
     *
     * @param card A card object
     */
    public void addCard(Card card) {
        this.cards.add(card);
        notifyItemInserted(cards.size() + 1);
    }

    /**
     * Add multiple cards to the current card list
     *
     * @param cards A list of card objects
     */
    public void addCards(List<Card> cards) {
        for (Card card : cards) {
            this.cards.add(card);
            notifyItemInserted(cards.size());
        }
        notifyDataSetChanged();
    }

    /**
     * @return The number of card objects in the data set
     */
    @Override
    public int getItemCount() {
        if (cards == null) return 0;
        else return cards.size ();
    }

    /**
     * @return A viewholder with the inflated layout resource view
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(layoutResource, viewGroup, false);

        return new ViewHolder(v);
    }

    /**
     * On bind to a viewholder, set the correct card according to the index
     * and update the selected item
     *
     * @param viewHolder The current viewholder
     * @param index The item index in the viewholder
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int index) {
        Card card = cards.get(index);
        viewHolder.setCard(card);
        // Get the current card view and if it is selected, show the selected
        // tick and don't show if it is not
        IconicsTextView selectedIcon =
          (IconicsTextView)viewHolder.itemView.findViewById(R.id.SELECTED_CARD);
        if (index == selectedItem) {
            selectedIcon.setVisibility(View.VISIBLE);
        }
        else if (index != selectedItem) {
            selectedIcon.setVisibility(View.GONE);
        }
    }

    /**
     * Deselect all cards in the adapter
     */
    public void deselectAll() {
        selectedItem = -1;
    }

    /**
     * ViewHolder object to hold the card information
     */
    public class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Card c;
        private TextView cardHolderName, cardExpiry, cardNumber;
        public IconicsTextView selectedIcon;

        /**
         * Constructor that requires an item view and initialises the textviews
         * to assign information to
         */
        public ViewHolder(View itemView) {
            super(itemView);

            cardHolderName =
                    (TextView) itemView.findViewById(R.id.card_tv_card_name);
            cardExpiry =
                    (TextView) itemView.findViewById(R.id.card_tv_card_expiry);
            cardNumber =
                    (TextView) itemView.findViewById(R.id.card_tv_card_number);
            selectedIcon =
                    (IconicsTextView) itemView.findViewById(R.id.SELECTED_CARD);

            itemView.setOnClickListener(this);
        }

        /**
         * On setting the card, update the information on the view
         *
         * @param c A card object c
         */
        public void setCard(Card c) {
            this.c = c;

            cardHolderName.setText("Name: "+c.getCardName());
            cardExpiry.setText("Expiry date: "+
                               String.valueOf(c.getCardExpiry()));
            cardNumber.setText("XXXX - XXXX - XXXX - "+c.getId().substring(12));
            selectedIcon.setVisibility(View.GONE);

            itemView.setTag(c);
        }

        /**
         * On clicking the card, change the current selected item and remove
         * the old one, then notify the onItemClickedListener so that the
         * parent RecyclerView can access the selected card information
         *
         * @param v The clicked view v
         */
        @Override
        public void onClick(View v){
            if (selectedItem != -1)
                notifyItemChanged(selectedItem);
                selectedItem = recyclerView.getChildPosition(v);
                notifyItemChanged(selectedItem);
            if (selectedItem == -1) {
                selectedItem = recyclerView.getChildPosition(v);
                notifyItemChanged(selectedItem);
            }
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getPosition(), this.c);
            }
        }

        /**
         * @return The current card object
         */
        public Card getCard() {return this.c;}
    }

    /**
     * An interface that detects when an item is clicked and gets the card
     * object that is clicked
     */
    public interface OnItemClickListener {
        public void onItemClick(View view , int position, Card card);
    }

    /**
     * Set an onItemClickListener to detect changes in the item's selected
     */
    public void SetOnItemClickListener(
                              final OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
