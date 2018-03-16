package com.sdsmdg.bookshareapp.BSA.ui.adapter.Local;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.BookAddDeleteResponse;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.RemoveBook;
import com.sdsmdg.bookshareapp.BSA.api.models.VerifyToken.Detail;
import com.sdsmdg.bookshareapp.BSA.ui.MyProfile;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private static final int PENDING_REMOVAL_TIMEOUT = 1500;
    private Book tempValues = null;
    private List<Book> itemsPendingRemoval;
    private String userId;
    private Context context;
    private List<Book> bookList;
    private List<Book> selectedBookList = new ArrayList<>();
    private ActionMode mActionMode;
    private SharedPreferences prefs;
    private boolean undoOn = true;
    private boolean isMultiSelect = false;
    private List<Book> booksToDelete = new ArrayList<>();

    /**
     * is undo on, you can turn it on from the toolbar menu
     * this array stores whether the current book is selected or not
     */
    //This activity reference is required to activate the contextual action bar
    private Activity activity;

    private Handler handler = new Handler(); // handler for running delayed runnables
    private HashMap<Book, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

    public BookAdapter(Context context, String userId, List<Book> bookList, Activity activity) {
        itemsPendingRemoval = new ArrayList<>();
        this.bookList = bookList;
        this.userId = userId;
        this.context = context;
        this.activity = activity;
        prefs = context.getSharedPreferences("Token", Context.MODE_PRIVATE);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout bookLayout, deleteLayout;
        TextView titleBook;
        TextView authorBook;
        ImageView imageBook;
        RatingBar ratingBook;
        TextView ratingCount;
        Button undoButton;

        ViewHolder(View itemView) {
            super(itemView);
            bookLayout = (LinearLayout) itemView.findViewById(R.id.book_layout);
            deleteLayout = (LinearLayout) itemView.findViewById(R.id.delete_layout);
            titleBook = (TextView) itemView.findViewById(R.id.book_title);
            authorBook = (TextView) itemView.findViewById(R.id.author);
            imageBook = (ImageView) itemView.findViewById(R.id.book_cover);
            ratingBook = (RatingBar) itemView.findViewById(R.id.book_rating);
            ratingCount = (TextView) itemView.findViewById(R.id.ratings_count);
            undoButton = (Button) itemView.findViewById(R.id.undo_button);
        }
    }

    //This function is used to delete the selected items
    private void deleteSelectedItem() {
        booksToDelete.addAll(selectedBookList);
        notifyDataSetChanged();
        for (Book book : selectedBookList) {
            remove(book);
        }
    }

    //This method is called after the contextual action bar is disabled
    public void reset() {
        isMultiSelect = false;
        selectedBookList = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_mybooks, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ViewHolder viewHolder = holder;
        final Book rbook = bookList.get(position);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMultiSelect){
                    multiSelect(viewHolder.getAdapterPosition());
                }
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isMultiSelect) {
                    isMultiSelect = true;
                    if (mActionMode == null) {
                        mActionMode = activity.startActionMode(mActionModeCallback);
                    }
                }
                multiSelect(viewHolder.getAdapterPosition());
                return true;
            }
        });
        if (booksToDelete.contains(rbook)){
            viewHolder.bookLayout.setAlpha(0.25f);
            viewHolder.deleteLayout.setVisibility(View.VISIBLE);
            viewHolder.itemView.setOnLongClickListener(null);
        }else{
            viewHolder.bookLayout.setAlpha(1f);
            viewHolder.deleteLayout.setVisibility(View.GONE);
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!isMultiSelect) {
                        isMultiSelect = true;
                        if (mActionMode == null) {
                            mActionMode = activity.startActionMode(mActionModeCallback);
                        }
                    }
                    multiSelect(viewHolder.getAdapterPosition());
                    return true;
                }
            });
        }
        if (itemsPendingRemoval.contains(rbook)) {
            // we need to show the "undo" state of the row
            viewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.delete2));
            viewHolder.titleBook.setVisibility(View.INVISIBLE);
            viewHolder.authorBook.setText("Delete Book ?");
            viewHolder.authorBook.setTextSize(20);
            viewHolder.ratingCount.setVisibility(View.INVISIBLE);
            viewHolder.ratingBook.setVisibility(View.INVISIBLE);
            viewHolder.imageBook.setVisibility(View.INVISIBLE);
            viewHolder.undoButton.setVisibility(View.VISIBLE);
            viewHolder.undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // user wants to undo the removal, let's cancel the pending task
                    Runnable pendingRemovalRunnable = pendingRunnables.get(rbook);
                    pendingRunnables.remove(rbook);
                    if (pendingRemovalRunnable != null)
                        handler.removeCallbacks(pendingRemovalRunnable);
                    itemsPendingRemoval.remove(rbook);
                    // this will rebind the row in "normal" state
                    notifyItemChanged(bookList.indexOf(rbook));
                }
            });

        } else {
            // we need to show the "normal" state
            tempValues = bookList.get(position);
            viewHolder.titleBook.setText(tempValues.getTitle());
            viewHolder.authorBook.setTextSize(14);
            viewHolder.authorBook.setText(tempValues.getAuthor());
            if (!tempValues.getGrImgUrl().isEmpty()) {
                Picasso.with(context).load(tempValues.getGrImgUrl()).placeholder(R.drawable.default_book_image).into(viewHolder.imageBook);
            }
            viewHolder.ratingBook.setRating(tempValues.getRating());
            viewHolder.ratingCount.setText("(" + tempValues.getRatingsCount() + ")");
            //if the book is selected, change it's color to holo blue light
            if (selectedBookList.contains(bookList.get(position))){
                viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.delete_gray));
            }else{
                viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.White));
            }
            viewHolder.titleBook.setVisibility(View.VISIBLE);
            viewHolder.authorBook.setVisibility(View.VISIBLE);
            viewHolder.ratingBook.setVisibility(View.VISIBLE);
            viewHolder.ratingCount.setVisibility(View.VISIBLE);
            viewHolder.imageBook.setVisibility(View.VISIBLE);
            viewHolder.undoButton.setVisibility(View.GONE);
            viewHolder.undoButton.setOnClickListener(null);
        }
    }

    private void multiSelect(int adapterPosition) {
        if (mActionMode != null){
            if (selectedBookList.contains(bookList.get(adapterPosition))){
                selectedBookList.remove(bookList.get(adapterPosition));
            } else {
                selectedBookList.add(bookList.get(adapterPosition));
            }
            if (selectedBookList.size() > 1) {
                mActionMode.setTitle(selectedBookList.size() + " items selected");
            } else if (selectedBookList.size() > 0) {
                mActionMode.setTitle(selectedBookList.size() + " item selected");
            } else {
                mActionMode.finish();
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        if (bookList != null)
            return bookList.size();

        return 0;
    }

    private boolean isUndoOn() {
        return undoOn;
    }

    private void pendingRemoval(int position) {
        final Book rbook = bookList.get(position);

        if (!itemsPendingRemoval.contains(rbook)) {
            itemsPendingRemoval.add(rbook);
            // this will redraw row in "undo" state
            notifyDataSetChanged();
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    booksToDelete.add(rbook);
                    notifyDataSetChanged();
                    remove(rbook);
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(rbook, pendingRemovalRunnable);
        }
    }

    private void remove(Book rbook) {
        removeBook(rbook.getId(), rbook);
    }

    private void removeBook(final String bookId, final Book rbook) {

        RemoveBook removeBook = new RemoveBook();
        removeBook.setBookId(bookId);
        removeBook.setUserId(userId);
        UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<BookAddDeleteResponse> call = usersAPI.removeBook(removeBook, "Token " + prefs.getString("token", null));
        call.enqueue(new Callback<BookAddDeleteResponse>() {
            @Override
            public void onResponse(Call<BookAddDeleteResponse> call, Response<BookAddDeleteResponse> response) {
                if (response.body() != null) {
                    String detail = response.body().getDetail();
                    Toast.makeText(context, detail, Toast.LENGTH_SHORT).show();
                    if (detail.equals("Successfully Removed!!")) {
                        //Make corresponding changes in MyProfile activity when a book is removed
                        ((MyProfile)context).onBookRemoved();
                        booksToDelete.remove(rbook);
                        if (itemsPendingRemoval.contains(rbook)) {
                            itemsPendingRemoval.remove(rbook);
                        }
                        if (bookList.contains(rbook)) {
                            bookList.remove(rbook);
                        }
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<BookAddDeleteResponse> call, Throwable t) {
                itemsPendingRemoval.remove(rbook);
                booksToDelete.remove(rbook);
                //This line will remove the undo button and show the book row completely
                notifyDataSetChanged();
                Log.i("book_size", Integer.toString(bookList.size()));
                Toast.makeText(context, R.string.connection_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isPendingRemoval(int position) {
        Book rbook = bookList.get(position);
        return itemsPendingRemoval.contains(rbook);
    }


    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.contextual_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_delete:
                    deleteSelectedItem();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            reset();
            mActionMode = null;
        }
    };

    public void setUpItemTouchHelper(RecyclerView mRecyclerView) {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(context.getResources().getColor(R.color.delete2));
                xMark = ContextCompat.getDrawable(context, R.drawable.ic_clear_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) context.getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                // BookAdapter bookAdapter = (BookAdapter) recyclerView.getAdapter();
                if (isUndoOn() && isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                // BookAdapter adapter = (BookAdapter) mRecyclerView.getAdapter();
                boolean undoOn = isUndoOn();
                if (undoOn) {
                    pendingRemoval(swipedPosition);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    public void setUpAnimationDecoratorHelper(RecyclerView mRecyclerView) {
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(context.getResources().getColor(R.color.delete2));
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }
        });
    }
}
