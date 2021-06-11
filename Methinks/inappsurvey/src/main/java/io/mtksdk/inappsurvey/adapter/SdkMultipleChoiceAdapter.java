package io.mtksdk.inappsurvey.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.mtksdk.inappsurvey.R;
import io.mtksdk.inappsurvey.ViewControllerManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import io.mtksdk.inappsurvey.converter.Question;

/**
 * Created by kgy 2019. 9. 24.
 */

public class SdkMultipleChoiceAdapter extends Section {
    private String questionText;
    private ArrayList<String> choices;
    private LinearLayout openEndEditTextContainer;
    private EditText openEndEditText;
    private Context context;
    private HashMap<Integer, ViewHolder> viewHolders;
    private boolean isClickedLastTextEntry;
    private ArrayList<Integer> selectedPosition;
    private HashMap<Integer, ImageView> checkImages;
    private HashMap<Integer, LinearLayout> mainLayouts;
    private Boolean multipleSelection;
    private Boolean isShuffleOrder;
    private HashMap<String, ArrayList<Object>> answerMap;
    private String objId;
    private Question question;
    private boolean isBinding;


    public SdkMultipleChoiceAdapter(Context context, Question question) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.sdk_cell_question_multiple_choice)
                .headerResourceId(R.layout.sdk_cell_question_header)
                .build());
        this.question = question;
        this.objId = question.getQuestionId();
        this.context = context;
        this.questionText = question.getText();
        this.choices = question.getFinalChoices();
        this.viewHolders = new HashMap<>();
        this.checkImages = new HashMap<>();
        this.mainLayouts = new HashMap<>();
        this.selectedPosition = new ArrayList<>();
        this.multipleSelection = (Boolean) question.getRule().get("allowMultipleSelection");
        this.isBinding = false;
    }

    @Override
    public int getContentItemsTotal() {
        return choices.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view){
        return new QuestionHeaderViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder itemHolder = (ViewHolder) holder;
        if(!viewHolders.containsKey(position)){
            isBinding = true;
//            viewHolders.put(position, itemHolder);
        } else {
            isBinding = false;
        }

        if (isBinding) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)itemHolder.itemView.getLayoutParams();
            if(position == 0){  // for top margin
                params.setMargins(0, (int) convertDpToPixel(context, 24), 0, 0);
            }else{
                params.setMargins(0, 0, 0, 0);
            }

            itemHolder.itemView.setLayoutParams(params);
            itemHolder.textContainer.setVisibility(View.VISIBLE);
            itemHolder.openEndAnswerForm.setVisibility(View.GONE);
            hideKeyboard();
            itemHolder.isOpenEndType = false;
            itemHolder.choice.setVisibility(View.VISIBLE);
            itemHolder.choice.setText(choices.get(position));
            itemHolder.choice.setTextColor(context.getResources().getColor(R.color.baseTextColor));
            itemHolder.openEndAnswerForm.setVisibility(View.GONE);
            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideKeyboard();
                    handleSelectedPosition(multipleSelection, true, position, itemHolder);
                }
            });
            isBinding = false;
        }

        if(selectedPosition.contains(position)){    // current position contained into selected
            makeViewHolderSelected(itemHolder, true);
        }else{
            makeViewHolderSelected(itemHolder, false);
        }
        viewHolders.put(position, itemHolder);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        final QuestionHeaderViewHolder headerHolder = (QuestionHeaderViewHolder) holder;
        headerHolder.questionContent.setText(questionText);
    }

    /**
     * hiding keyboard
     */
    public void hideKeyboard(){
        if(openEndEditText != null){
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(openEndEditText.getApplicationWindowToken(), 0);
        }
    }

    /**
     * converting dp to pixel for the layout params for choice view
     * @param context
     * @param dp
     * @return
     */
    public static float convertDpToPixel(Context context, float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     *
     */
    private void handleSelectedPosition(boolean isMultiple, boolean isAdd, int position, ViewHolder viewHolder) {
        if (isMultiple) { //multiple selection
            if (selectedPosition.contains(position)){    // exist selected position(check remove action)
                selectedPosition.remove(selectedPosition.indexOf(position));
                makeViewHolderSelected(viewHolder, false);
            } else  {
                selectedPosition.add(position);
                makeViewHolderSelected(viewHolder, true);
            }
        } else { //single selection
            if(selectedPosition.contains(position)) {
                selectedPosition.clear();
                if (selectedPosition.size() == 0) {
                    Log.e("Single", "resetting the position worked");
                }
                makeViewHolderSelected(viewHolder, false);
            } else {
                for (Integer index : viewHolders.keySet()) {
                    ViewHolder currViewHolder = viewHolders.get(index);
                    if (index + 1 == currViewHolder.getAdapterPosition()) {
                        makeViewHolderSelected(currViewHolder, index == position ? true : false);
                    }
                }
                selectedPosition.clear();
                selectedPosition.add(position);
            }
        }

        ArrayList<Object> answer = new ArrayList<>();

        for (Integer index : selectedPosition) {
            Log.e("answer", choices.get(index));
            answer.add(choices.get(index));
        }

        question.setAnswerMap(answer);
    }

    private void makeViewHolderSelected(ViewHolder viewHolder, boolean selected) {
        viewHolder.textContainer.setSelected(selected);
        viewHolder.selectionIcon.setSelected(selected);
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public ConstraintLayout mainLayout;
        public ImageView selectionIcon;
        public TextView choice;
        public LinearLayout openEndAnswerForm;
        public EditText answerForOpenEnd;
        public ImageView attachedImage;
        public ProgressBar pbLoading;
        public boolean isOpenEndType;
        public boolean isCreating;
        public boolean hasEntryAnswer;
        public View textContainer;
        public LinearLayout nonOpenEndtype;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mainLayout = itemView.findViewById(R.id.cell_multiple_choice_main_layout);
            this.textContainer = itemView.findViewById(R.id.ll_text_container);
            this.itemView = itemView;
            this.selectionIcon = itemView.findViewById(R.id.iv_choice);
            this.pbLoading = itemView.findViewById(R.id.pb_loading);
            this.attachedImage = itemView.findViewById(R.id.iv_image);
            this.choice = itemView.findViewById(R.id.tv_text);
            this.openEndAnswerForm = itemView.findViewById(R.id.ll_text_entry_container);
            this.nonOpenEndtype = itemView.findViewById(R.id.non_lastEntry);
            this.answerForOpenEnd = itemView.findViewById(R.id.ev_answer);
            this.isOpenEndType = false;
            this.isCreating = false;
            this.hasEntryAnswer = false;
//            openEndEditTextContainer = openEndAnswerForm;
        }
    }

    public class QuestionHeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView questionContent;
        public ImageView attachedImage;

        public QuestionHeaderViewHolder(View itemView) {
            super(itemView);
            this.questionContent = itemView.findViewById(R.id.question_content);
            this.attachedImage = itemView.findViewById(R.id.attached_image);
        }
    }

    public static boolean existValueInStringArray(List<String> items, String value){           //List has to be ArrayList when connected to the sever
        for(int i = 0; i < items.size(); i++){
            String item = items.get(i);
            if(item.equals(value)){
                return true;
            }
        }

        return false;
    }

    public boolean validate() {
        ArrayList<Object> answer = question.getAnswerMap().get(objId);
        if (answer == null || answer.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void shuffleArrayList(ArrayList arrayList){
        long seed = System.nanoTime();
        Collections.shuffle(arrayList, new Random(seed));
    }


}
