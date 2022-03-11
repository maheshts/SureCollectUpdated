package com.loan.recovery.contactslist;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loan.recovery.R;
import com.loan.recovery.activity.BaseActivity;
import com.loan.recovery.database.Recording;
import com.loan.recovery.database.RepositoryImpl;
import com.loan.recovery.recorder.Recorder;
import com.loan.recovery.util.AppConstants;

import java.io.File;
import java.util.List;

public class UnassignedRecordingsFragment extends Fragment {
    private View rootView;
    List<Recording> recordings;
    protected RecordingAdapter adapter;
    protected RecyclerView recordingsRecycler;
    protected BaseActivity parentActivity;
    private TextView noContent;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parentActivity = (BaseActivity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        RepositoryImpl repository = new RepositoryImpl(getActivity(), AppConstants.DATABASE_NAME);
        recordings = repository.getRecordings();
        if(recordings.size() > 0) {
            adapter = new RecordingAdapter(recordings);
            recordingsRecycler.setAdapter(adapter);
            recordingsRecycler.setVisibility(View.VISIBLE);
            noContent.setVisibility(View.GONE);
        }
        else {
            noContent.setVisibility(View.VISIBLE);
            recordingsRecycler.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.unassigned_recordings_fragment, container, false);
        recordingsRecycler = rootView.findViewById(R.id.unassigned_recordings);
        noContent = rootView.findViewById(R.id.no_content_detail);
        recordingsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recordingsRecycler.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        return rootView;
    }

    class RecordingHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView recordingType, recordingAdorn, exclamation;
        CheckBox checkBox;

        RecordingHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.recording, parent, false));
            recordingType = itemView.findViewById(R.id.recording_type);
            title = itemView.findViewById(R.id.recording_title);
            checkBox = itemView.findViewById(R.id.recording_checkbox);
            recordingAdorn = itemView.findViewById(R.id.recording_adorn);
            exclamation = itemView.findViewById(R.id.recording_exclamation);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Recording recording = adapter.getItem(getAdapterPosition());
            if (recording.exists()) {
                File file = new File(recording.getPath());
//                Intent playIntent = new Intent(parentActivity, PlayerActivity.class);
////                playIntent.putExtra(AppConstants.RECORDING_EXTRA, recording);
//                playIntent.putExtra("path", recording.getPath());
//                startActivity(playIntent);
            } else
                Toast.makeText(parentActivity, R.string.audio_file_missing, Toast.LENGTH_SHORT).show();
        }
    }

    public class RecordingAdapter extends RecyclerView.Adapter<RecordingHolder> {
        private List<Recording> recordings;

        RecordingAdapter(List<Recording> recordings) {
            this.recordings = recordings;
        }

        @Override
        @NonNull
        public RecordingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parentActivity);
            return new RecordingHolder(layoutInflater, parent);
        }

        public Recording getItem(int position) {
            return recordings.get(position);
        }

        @Override
        public void onBindViewHolder(@NonNull RecordingHolder holder, final int position) {
            final Recording recording = recordings.get(position);
            int adornRes;
//            switch (recording.getFormat()) {
//                case Recorder.WAV_FORMAT:
//                    adornRes = parentActivity.getSettedTheme().equals(BaseActivity.LIGHT_THEME) ?
//                            R.drawable.sound_symbol_wav_light : R.drawable.sound_symbol_wav_dark;
//                    break;
//                case Recorder.AAC_HIGH_FORMAT:
//                    adornRes = parentActivity.getSettedTheme().equals(BaseActivity.LIGHT_THEME) ?
//                            R.drawable.sound_symbol_aac128_light : R.drawable.sound_symbol_aac128_dark;
//                    break;
//                case Recorder.AAC_BASIC_FORMAT:
//                    adornRes = parentActivity.getSettedTheme().equals(BaseActivity.LIGHT_THEME) ?
//                            R.drawable.sound_symbol_aac32_light : R.drawable.sound_symbol_aac32_dark;
//                    break;
//                default:
//                    adornRes = parentActivity.getSettedTheme().equals(BaseActivity.LIGHT_THEME) ?
//                            R.drawable.sound_symbol_aac64_light : R.drawable.sound_symbol_aac64_dark;
//            }

            holder.title.setText(recording.getName()+"Hel");
            holder.recordingType.setImageResource(R.drawable.outgoing_dark);
            holder.recordingAdorn.setImageResource(R.drawable.sound_symbol_aac32_light);

            if (!recording.exists())
                markNonexistent(holder);
        }

        private void markNonexistent(RecordingHolder holder) {
            holder.exclamation.setVisibility(View.VISIBLE);
            int filter = parentActivity.getSettedTheme().equals(BaseActivity.LIGHT_THEME) ?
                    Color.argb(255, 0, 0, 0) : Color.argb(255, 255, 255, 255);
            holder.recordingAdorn.setColorFilter(filter);
            holder.recordingType.setColorFilter(filter);
            holder.recordingAdorn.setImageAlpha(100);
            holder.recordingType.setImageAlpha(100);
            holder.title.setAlpha(0.5f);
        }

        private void unMarkNonexistent(RecordingHolder holder) {
            holder.exclamation.setVisibility(View.GONE);
            holder.recordingAdorn.setColorFilter(null);
            holder.recordingType.setColorFilter(null);
            holder.recordingType.setImageAlpha(255);
            holder.recordingAdorn.setImageAlpha(255);
            holder.title.setAlpha(1f);
        }

        @Override
        public void onViewRecycled(@NonNull RecordingHolder holder) {
            super.onViewRecycled(holder);
            unMarkNonexistent(holder);
        }

        @Override
        public int getItemCount() {
            return recordings.size();
        }
    }
}