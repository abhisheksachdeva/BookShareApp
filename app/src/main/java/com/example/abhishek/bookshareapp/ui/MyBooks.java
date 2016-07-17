
package com.example.abhishek.bookshareapp.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.LocalBooks.Book;
import com.example.abhishek.bookshareapp.api.models.LocalBooks.RemoveBook;
import com.example.abhishek.bookshareapp.api.models.UserInfo;
import com.example.abhishek.bookshareapp.api.models.VerifyToken.Detail;
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
    ProgressDialog progress; // this is not used ,in this activity as of now...Just for testing purposes.
    ProgressBar prog;
    String Resp;
    TextView noItemsTextView;

    public String getResp() {
        return Resp;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);

        noItemsTextView = (TextView) findViewById(R.id.no_items_text);

        prog = (ProgressBar) findViewById(R.id.progress);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        new ProgressLoader().execute(15);


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

    class ProgressLoader extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            for (; count <= params[0]; count++) {
                try {
                    Thread.sleep(1000);
                    Log.d("MBAs", getResp() + "+" + count.toString());
                    if (getResp() != null) {
                        break;
                    }
                    publishProgress(count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (getResp() != null) {
                    break;
                }
            }


            return "Task Completed.";
        }

        @Override
        protected void onPostExecute(String result) {
            if (getResp() == null) {
                Toast.makeText(MyBooks.this, "Please Try Again.", Toast.LENGTH_SHORT).show();
                prog.setVisibility(View.INVISIBLE);
            } else {
                prog.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        protected void onPreExecute() {
//            progress=new ProgressDialog(MyBooks.this);
//            progress.setMessage("Wont Take Long Bruh!...");
//            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progress.setIndeterminate(true);
//            progress.setIndeterminateDrawable(getResources().getDrawable(R.drawable.loading));

            prog.setMax(5);
            prog.setProgress(0);
//            progress.setCancelable(false);
            prog.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
//            progress.setProgress(values[0]);
        }
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
                    Log.d("UserProfile Response:", response.toString());
                    Resp = response.toString();
                    List<Book> booksTempInfoList = response.body().getUserBookList();
                    if(booksTempInfoList.size() == 0) {
                        noItemsTextView.setVisibility(View.VISIBLE);
                    }
                    booksList.clear();
                    booksList.addAll(booksTempInfoList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.d("BookDetails fail", t.toString());
            }
        });
    }

    private void setUpRecyclerView(String id) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MyBooks.this));
        booksList = new ArrayList<>();
        adapter = new BookAdapter(id, booksList);
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
                background = new ColorDrawable(Color.GRAY);
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
                background = new ColorDrawable(Color.GRAY);
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

        private Handler handler = new Handler(); // hanlder for running delayed runnables
        HashMap<Book, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

        public BookAdapter(String userId, List<Book> bookList) {
            itemsPendingRemoval = new ArrayList<>();
            this.bookList = bookList;
            this.userId = userId;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BookViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            BookViewHolder viewHolder = (BookViewHolder) holder;
            final Book rbook = bookList.get(position);

            if (itemsPendingRemoval.contains(rbook)) {
                // we need to show the "undo" state of the row
                viewHolder.itemView.setBackgroundColor(Color.GRAY);
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

}
