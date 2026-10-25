package com.example.e_commerceapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.models.Review;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final Context context;
    private List<Review> reviews = new ArrayList<>();

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews != null ? reviews : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);

        holder.tvReviewerName.setText(review.getUserName() != null ? review.getUserName() : "Customer");
        holder.tvReviewComment.setText(review.getComment());
        holder.rbReviewRating.setRating(review.getRating());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        holder.tvReviewDate.setText(sdf.format(new Date(review.getCreatedAt())));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewerName, tvReviewComment, tvReviewDate;
        RatingBar rbReviewRating;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewerName = itemView.findViewById(R.id.tvReviewerName);
            tvReviewComment = itemView.findViewById(R.id.tvReviewComment);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            rbReviewRating = itemView.findViewById(R.id.rbReviewRating);
        }
    }
}