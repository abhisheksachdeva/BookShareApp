
package com.sdsmdg.bookshareapp.BSA.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.RemoveBook;
import com.sdsmdg.bookshareapp.BSA.api.models.UserInfo;
import com.sdsmdg.bookshareapp.BSA.api.models.VerifyToken.Detail;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyBooks extends AppCompatActivity {
    List<Book> booksList;
    BookAdapter adapter;
    RecyclerView mRecyclerView;
    Integer count = 1;
    String Resp;
    CustomProgressDialog customProgressDialog;

    ActionMode mActionMode;


    TextView noItemsTextView;
    public String getResp() {
        return Resp;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);

        customProgressDialog = new CustomProgressDialog(MyBooks.this);
        customProgressDialog.setCancelable(false);
        customProgressDialog.show();
        customProgressDialog.getWindow().setLayout(464, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);

        noItemsTextView = (TextView) findViewById(R.id.no_items_text);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        SharedPreferences preferences = getSharedPreferences("Token", MODE_PRIVATE);
        String id = preferences.getString("id", "");

        setUpRecyclerView(id);

        getUserBookList(id);

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyBooks.this, SearchResultsActivity.class);
                startActivity(i);
                finish();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    public void getUserBookList(String id) {
        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<UserInfo> call = api.getUserDetails(id);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.body() != null) {
                    Resp = response.toString();
                    List<Book> booksTempInfoList = response.body().getUserBookList();
                    if(booksTempInfoList.size() == 0) {
                        noItemsTextView.setVisibility(View.VISIBLE);
                    }
                    booksList.clear();
                    booksList.addAll(booksTempInfoList);
                    adapter.notifyDataSetChanged();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                    customProgressDialog.dismiss();

                        }
                    }, 1000);
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.d("MyBooksLoad fail", t.toString());
                customProgressDialog.dismiss();

            }
        });
    }

    private void setUpRecyclerView(String id) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MyBooks.this));
        booksList = new ArrayList<>();
        adapter = new BookAdapter(id, booksList, this);
        mRecyclerView.setAdapter(adapter);
        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();
    }

    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(MyBooks.this, R.drawable.ic_clear_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) MyBooks.this.getResources().getDimension(R.dimen.ic_clear_margin);
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
                BookAdapter bookAdapter = (BookAdapter) recyclerView.getAdapter();
                if (bookAdapter.isUndoOn() && bookAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                BookAdapter adapter = (BookAdapter) mRecyclerView.getAdapter();
                boolean undoOn = adapter.isUndoOn();
                if (undoOn) {
                    adapter.pendingRemoval(swipedPosition);
                } else {
                    adapter.remove(swipedPosition);
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

    private void setUpAnimationDecoratorHelper() {
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
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

    class BookAdapter extends RecyclerView.Adapter {

        private static final int PENDING_REMOVAL_TIMEOUT = 5000;
        Book tempValues = null;
        List<Book> itemsPendingRemoval;
        String userId;
        private List<Book> bookList;
        boolean undoOn = true; // is undo on, you can turn it on from the toolbar menu
        //this array stores whether the current book is selected or not
        SparseBooleanArray selected;
        //This activity reference is required to activate the contextual action bar
        Activity activity;

        private Handler handler = new Handler(); // hanlder for running delayed runnables
        HashMap<Book, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

        public BookAdapter(String userId, List<Book> bookList, Activity activity) {
            itemsPendingRemoval = new ArrayList<>();
            this.bookList = bookList;
            selected = new SparseBooleanArray();
            this.userId = userId;
            this.activity = activity;
        }

        //This function is used to delete the selected items
        public void deleteSelectedItem() {
            for (int i = 0; i < bookList.size(); i++) {
                if(selected.get(i)) {
                    remove(i);
                }
            }
        }

        //This method is called after the contextual action bar is disabled
        public void reset() {
            selected.clear();
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BookViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final BookViewHolder viewHolder = (BookViewHolder) holder;
            final Book rbook = bookList.get(position);

            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getApplicationContext(), "Selected", Toast.LENGTH_SHORT).show();
                    //This line activates the contextual action bar
                    mActionMode = activity.startActionMode(mActionModeCallback);
                    selected.put(position, true);
                    viewHolder.itemView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                    return false;
                }
            });

            if (itemsPendingRemoval.contains(rbook)) {
                // we need to show the "undo" state of the row
                viewHolder.itemView.setBackgroundColor(Color.RED);
                viewHolder.titleBook.setVisibility(View.INVISIBLE);
                viewHolder.authorBook.setText("Delete Book ?");
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
                viewHolder.authorBook.setText(tempValues.getAuthor());
                if (!tempValues.getGrImgUrl().isEmpty()) {
                    Picasso.with(MyBooks.this).load(tempValues.getGrImgUrl()).placeholder(R.drawable.default_book_image).into(viewHolder.imageBook);
                }
                viewHolder.ratingBook.setRating(tempValues.getRating());
                viewHolder.ratingCount.setText(tempValues.getRatingsCount() + " votes");
                viewHolder.itemView.setBackgroundColor(Color.WHITE);
                //if the book is selected, change it's color to holo blue light
                if(selected.get(position)) {
                    viewHolder.itemView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
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

        @Override
        public int getItemCount() {
            if (bookList != null)
                return bookList.size();

            return 0;
        }

        public boolean isUndoOn() {
            return undoOn;
        }

        public void pendingRemoval(int position) {
            final Book rbook = bookList.get(position);

            if (!itemsPendingRemoval.contains(rbook)) {
                itemsPendingRemoval.add(rbook);
                // this will redraw row in "undo" state
                notifyItemChanged(position);
                // let's create, store and post a runnable to remove the item
                Runnable pendingRemovalRunnable = new Runnable() {
                    @Override
                    public void run() {
                        remove(bookList.indexOf(rbook));
                    }
                };
                handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
                pendingRunnables.put(rbook, pendingRemovalRunnable);
            }
        }

        public void remove(int position) {
            Book rbook = bookList.get(position);
            removeBook(rbook.getId());
            if (itemsPendingRemoval.contains(rbook)) {
                itemsPendingRemoval.remove(rbook);
            }
            if (bookList.contains(rbook)) {
                bookList.remove(position);
                notifyItemRemoved(position);
            }
        }

        public void removeBook(String bookId) {

            RemoveBook removeBook = new RemoveBook();
            removeBook.setBookId(bookId);
            removeBook.setUserId(userId);

            UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
            Call<Detail> call = usersAPI.removeBook(removeBook);
            call.enqueue(new Callback<Detail>() {
                @Override
                public void onResponse(Call<Detail> call, Response<Detail> response) {
                    if (response.body() != null) {
                        notifyDataSetChanged();
                        Toast.makeText(MyBooks.this, "Successfully removed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Detail> call, Throwable t) {
                    Toast.makeText(MyBooks.this, "Check your network connectivity and try again", Toast.LENGTH_SHORT).show();
                }
            });
        }

        public boolean isPendingRemoval(int position) {
            Book rbook = bookList.get(position);
            return itemsPendingRemoval.contains(rbook);
        }
    }

    /**
     * ViewHolder capable of presenting two states: "normal" and "undo" state.
     */
    static class BookViewHolder extends RecyclerView.ViewHolder {

        public TextView titleBook;
        public TextView authorBook;
        public ImageView imageBook;
        public RatingBar ratingBook;
        public TextView ratingCount;
        Button undoButton;

        public BookViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_mybooks, parent, false));
            titleBook = (TextView) itemView.findViewById(R.id.book_title);
            authorBook = (TextView) itemView.findViewById(R.id.author);
            imageBook = (ImageView) itemView.findViewById(R.id.book_cover);
            ratingBook = (RatingBar) itemView.findViewById(R.id.book_rating);
            ratingCount = (TextView) itemView.findViewById(R.id.ratings_count);
            undoButton = (Button) itemView.findViewById(R.id.undo_button);
        }


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
                    adapter.deleteSelectedItem();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.reset();
            mActionMode = null;
        }
    };

}