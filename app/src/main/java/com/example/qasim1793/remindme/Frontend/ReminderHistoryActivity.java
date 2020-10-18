package com.example.qasim1793.remindme.Frontend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.example.qasim1793.remindme.Backend.AlarmReceiver;
import com.example.qasim1793.remindme.Backend.DateTimeSorter;
import com.example.qasim1793.remindme.Backend.Reminder;
import com.example.qasim1793.remindme.Backend.ReminderDatabase;
import com.example.qasim1793.remindme.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

public class ReminderHistoryActivity extends AppCompatActivity {
        private RecyclerView mList;
        private SimpleAdapter mAdapter;
        private Toolbar mToolbar;
        private TextView mNoReminderView;
        //    private FloatingActionButton mAddReminderButton;
        private int mTempPost;
        private LinkedHashMap<Integer, Integer> IDmap = new LinkedHashMap<>();
        private ReminderDatabase rb;
        private MultiSelector mMultiSelector = new MultiSelector();
        private AlarmReceiver mAlarmReceiver;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_reminder);

            rb = new ReminderDatabase(getApplicationContext());


//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        mAddReminderButton = (FloatingActionButton) findViewById(R.id.add_reminder);
            mList = (RecyclerView) findViewById(R.id.reminder_list);
            mNoReminderView = (TextView) findViewById(R.id.no_reminder_text);


            List<Reminder> mTest = rb.getAllReminders();

            if (mTest.isEmpty()) {
                mNoReminderView.setVisibility(View.VISIBLE);
            }


            mList.setLayoutManager(getLayoutManager());
            registerForContextMenu(mList);
            mAdapter = new SimpleAdapter();
            mAdapter.setItemCount(getDefaultItemCount());
            mList.setAdapter(mAdapter);


//        setSupportActionBar(mToolbar);
//        mToolbar.setTitle(R.string.app_name);

//        mAddReminderButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), ReminderAddActivity.class);
//                startActivity(intent);
//            }
//        });


            mAlarmReceiver = new AlarmReceiver();
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        }

        private android.support.v7.view.ActionMode.Callback mDeleteMode = new ModalMultiSelectorCallback(mMultiSelector) {

            @Override
            public boolean onCreateActionMode(android.support.v7.view.ActionMode actionMode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(android.support.v7.view.ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.discard_reminder:

                        actionMode.finish();


                        for (int i = IDmap.size(); i >= 0; i--) {
                            if (mMultiSelector.isSelected(i, 0)) {
                                int id = IDmap.get(i);


                                Reminder temp = rb.getReminder(id);

                                rb.deleteReminder(temp);

                                mAdapter.removeItemSelected(i);

                                mAlarmReceiver.cancelAlarm(getApplicationContext(), id);
                            }
                        }


                        mMultiSelector.clearSelections();

                        mAdapter.onDeleteItem(getDefaultItemCount());


                        Toast.makeText(getApplicationContext(),
                                "Deleted",
                                Toast.LENGTH_SHORT).show();


                        List<Reminder> mTest = rb.getAllReminders();

                        if (mTest.isEmpty()) {
                            mNoReminderView.setVisibility(View.VISIBLE);
                        } else {
                            mNoReminderView.setVisibility(View.GONE);
                        }

                        return true;


                    case R.id.save_reminder:

                        actionMode.finish();

                        mMultiSelector.clearSelections();
                        return true;

                    default:
                        break;
                }
                return false;
            }
        };


        private void selectReminder(int mClickID) {
            String mStringClickID = Integer.toString(mClickID);


            Intent i = new Intent(this, ReminderEditActivity.class);
            i.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, mStringClickID);
            startActivityForResult(i, 1);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            mAdapter.setItemCount(getDefaultItemCount());
        }

        @Override
        public void onResume(){
            super.onResume();

            List<Reminder> mTest = rb.getAllReminders();

            if (mTest.isEmpty()) {
                mNoReminderView.setVisibility(View.VISIBLE);
            } else {
                mNoReminderView.setVisibility(View.GONE);
            }

            mAdapter.setItemCount(getDefaultItemCount());
        }


        protected RecyclerView.LayoutManager getLayoutManager() {
            return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        }

        protected int getDefaultItemCount() {
            return 100;
        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.main_menu, menu);
            return true;
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {


            return super.onOptionsItemSelected(item);
        }



public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.VerticalItemHolder> {
    private ArrayList<SimpleAdapter.ReminderItem> mItems;

    public SimpleAdapter() {
        mItems = new ArrayList<>();
    }

    public void setItemCount(int count) {
        mItems.clear();
        mItems.addAll(generateData(count));
        notifyDataSetChanged();
    }

    public void onDeleteItem(int count) {
        mItems.clear();
        mItems.addAll(generateData(count));
    }

    public void removeItemSelected(int selected) {
        if (mItems.isEmpty()) return;
        mItems.remove(selected);
        notifyItemRemoved(selected);
    }


    @Override
    public SimpleAdapter.VerticalItemHolder onCreateViewHolder(ViewGroup container, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View root = inflater.inflate(R.layout.recycle_items, container, false);

        return new SimpleAdapter.VerticalItemHolder(root, this);
    }

    @Override
    public void onBindViewHolder(SimpleAdapter.VerticalItemHolder itemHolder, int position) {
        SimpleAdapter.ReminderItem item = mItems.get(position);
        itemHolder.setReminderTitle(item.mTitle);
        itemHolder.setReminderDateTime(item.mDateTime);
        itemHolder.setReminderRepeatInfo(item.mRepeat, item.mRepeatNo, item.mRepeatType);
        itemHolder.setActiveImage(item.mActive);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    public  class ReminderItem {
        public String mTitle;
        public String mDateTime;
        public String mRepeat;
        public String mRepeatNo;
        public String mRepeatType;
        public String mActive;
//            public String reminderCategory;
//            public String favourite;

        public ReminderItem(String Title, String DateTime, String Repeat, String RepeatNo, String RepeatType, String Active){//,String category,String favourite) {
            this.mTitle = Title;
            this.mDateTime = DateTime;
            this.mRepeat = Repeat;
            this.mRepeatNo = RepeatNo;
            this.mRepeatType = RepeatType;
            this.mActive = Active;
//                this.reminderCategory = category;
//                this.favourite = favourite;
        }
    }


    public class DateTimeComparator implements Comparator {
        DateFormat f = new SimpleDateFormat("dd/mm/yyyy hh:mm");

        public int compare(Object a, Object b) {
            String o1 = ((DateTimeSorter)a).getDateTime();
            String o2 = ((DateTimeSorter)b).getDateTime();

            try {
                return f.parse(o1).compareTo(f.parse(o2));
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public  class VerticalItemHolder extends SwappingHolder
            implements View.OnClickListener, View.OnLongClickListener {
        private TextView mTitleText, mDateAndTimeText, mRepeatInfoText;
        private ImageView mActiveImage , mThumbnailImage;
        private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
        private TextDrawable mDrawableBuilder;
        private SimpleAdapter mAdapter;

        public VerticalItemHolder(View itemView, SimpleAdapter adapter) {
            super(itemView, mMultiSelector);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setLongClickable(true);


            mAdapter = adapter;


            mTitleText = (TextView) itemView.findViewById(R.id.recycle_title);
            mDateAndTimeText = (TextView) itemView.findViewById(R.id.recycle_date_time);
            mRepeatInfoText = (TextView) itemView.findViewById(R.id.recycle_repeat_info);
            mActiveImage = (ImageView) itemView.findViewById(R.id.active_image);
            mThumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnail_image);
        }


        @Override
        public void onClick(View v) {
            if (!mMultiSelector.tapSelection(this)) {
                mTempPost = mList.getChildAdapterPosition(v);

                int mReminderClickID = IDmap.get(mTempPost);
                selectReminder(mReminderClickID);

            } else if(mMultiSelector.getSelectedPositions().isEmpty()){
                mAdapter.setItemCount(getDefaultItemCount());
            }
        }


        @Override
        public boolean onLongClick(View v) {
            AppCompatActivity activity = ReminderHistoryActivity.this;
            activity.startSupportActionMode(mDeleteMode);
            mMultiSelector.setSelected(this, true);
            return true;
        }


        public void setReminderTitle(String title) {
            mTitleText.setText(title);
            String letter = "A";

            if(title != null && !title.isEmpty()) {
                letter = title.substring(0, 1);
            }

            int color = mColorGenerator.getRandomColor();

            mDrawableBuilder = TextDrawable.builder()
                    .buildRound(letter, color);
            mThumbnailImage.setImageDrawable(mDrawableBuilder);
        }

        public void setReminderDateTime(String datetime) {
            mDateAndTimeText.setText(datetime);
        }

        public void setReminderRepeatInfo(String repeat, String repeatNo, String repeatType) {
            if(repeat.equals("true")){
                mRepeatInfoText.setText("Every " + repeatNo + " " + repeatType + "(s)");
            }else if (repeat.equals("false")) {
                mRepeatInfoText.setText("Repeat Off");
            }
        }

        public void setActiveImage(String active){
            if(active.equals("true")){
                mActiveImage.setImageResource(R.drawable.ic_notifications_on_red_24dp);
            }else if (active.equals("false")) {
                mActiveImage.setImageResource(R.drawable.ic_notifications_off_red_24dp);
            }
        }

        public void setCategory(String category){

        }

        public void setFavourite(String favourite){

        }
    }

    public SimpleAdapter.ReminderItem generateDummyData() {
        return new SimpleAdapter.ReminderItem("1", "2", "3", "4", "5", "6");
    }

    public List<SimpleAdapter.ReminderItem> generateData(int count) {
        ArrayList<SimpleAdapter.ReminderItem> items = new ArrayList<>();

        List<Reminder> reminders = rb.getAllReminders();

        List<String> Titles = new ArrayList<>();
        List<String> Repeats = new ArrayList<>();
        List<String> RepeatNos = new ArrayList<>();
        List<String> RepeatTypes = new ArrayList<>();
        List<String> Actives = new ArrayList<>();
        List<String> DateAndTime = new ArrayList<>();
        List<Integer> IDList= new ArrayList<>();
        List<DateTimeSorter> DateTimeSortList = new ArrayList<>();

        for (Reminder r : reminders) {
            Titles.add(r.getTitle());
            DateAndTime.add(r.getDate() + " " + r.getTime());
            Repeats.add(r.getRepeat());
            RepeatNos.add(r.getRepeatNo());
            RepeatTypes.add(r.getRepeatType());
            Actives.add(r.getActive());
            IDList.add(r.getID());
        }

        int key = 0;

        for(int k = 0; k<Titles.size(); k++){
            DateTimeSortList.add(new DateTimeSorter(key, DateAndTime.get(k)));
            key++;
        }

        Collections.sort(DateTimeSortList, new SimpleAdapter.DateTimeComparator());

        int k = 0;

        for (DateTimeSorter item:DateTimeSortList) {
            int i = item.getIndex();

            items.add(new SimpleAdapter.ReminderItem(Titles.get(i), DateAndTime.get(i), Repeats.get(i),
                    RepeatNos.get(i), RepeatTypes.get(i), Actives.get(i)));
            IDmap.put(k, IDList.get(i));
            k++;
        }
        return items;
    }
}
}
