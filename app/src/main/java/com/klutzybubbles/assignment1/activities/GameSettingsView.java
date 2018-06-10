package com.klutzybubbles.assignment1.activities;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.klutzybubbles.assignment1.interfaces.OnNavigationClickListener;

import petrov.kristiyan.colorpicker.ColorPicker;
import petrov.kristiyan.colorpicker.ColorUtils;

/**
 * <h1>GameSettingsView.java</h1>
 * Class used as the Fragment for the Settings Screen
 *
 * @author Lee Tzilantonis
 * @version 1.0.0
 * @since 10/6/2018
 * @see android.support.v4.app.Fragment
 */
public class GameSettingsView extends android.support.v4.app.Fragment {

    /**
     * Main view that holds all the Settings Screen content
     */
    private View view;

    /**
     * Called when the View within the fragment is to be inflated
     *
     * @param i - The LayoutInflater to be used
     * @param g - The ViewGroup the view should be contained within
     * @param b - The Bundle object parsed to the View
     * @return - The created View
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater i, ViewGroup g, Bundle b) {
        if (this.view == null)
            this.view = i.inflate(R.layout.activity_settings_screen, g, false);
        return this.view;
    }

    /**
     * Called when the containing activity has been created, letting us know is is safe to use
     * getActivity(). Due to the static nature of the Settings Screen all content is contained
     * within the onActivityCreated(Bundle) method
     *
     * @param savedInstanceState - The Bundle state parsed to the Activity
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.getView() == null || this.getActivity() == null)
            return;
        OnNavigationClickListener l;
        try {
            l = (OnNavigationClickListener) this.getActivity();
        } catch (ClassCastException e) {
            return;
        }

        final SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        //s.edit().clear().commit();

        if (this.view != null) {
            Button b = view.findViewById(R.id.button_home);
            b.setOnClickListener(v -> l.requestChange(SplashScreen.MAIN_MENU));
            final Button blank = view.findViewById(R.id.button_blank);
            blank.setOnClickListener(v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final ColorPicker c = new ColorPicker(this.getActivity());
                Log.i("GSH:onActivityC", s.getString(getString(R.string.pref_key_blank), getString(R.string.pref_default_blank)));
                c.setDefaultColorButton(Color.parseColor(s.getString(getString(R.string.pref_key_blank), getString(R.string.pref_default_blank))))
                        .setColors(R.array.colors)
                        .setColumns(5)
                        .setRoundColorButton(true)
                        .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                            @Override
                            public void onChooseColor(int position, int color) {
                                s.edit().putString(getString(R.string.pref_key_blank), String.format("#%06X", (0xFFFFFF & color))).apply();
                                blank.setBackgroundTintList(ColorStateList.valueOf(color));
                                blank.setTextColor(ColorUtils.isWhiteText(color) ? Color.WHITE : Color.BLACK);
                            }

                            @Override
                            public void onCancel() {}
                        }).show();
                } else {
                    Toast.makeText(blank.getContext(), "Cannot use color settings below android M", Toast.LENGTH_SHORT).show();
                }
            });
            final Button colA = view.findViewById(R.id.button_color_a);
            colA.setOnClickListener(v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final ColorPicker co = new ColorPicker(this.getActivity());
                Log.i("GSH:onActivityC", s.getString(getString(R.string.pref_key_color_a), getString(R.string.pref_default_color_a)));
                co.setDefaultColorButton(Color.parseColor(s.getString(getString(R.string.pref_key_color_a), getString(R.string.pref_default_color_a))))
                        .setColors(R.array.colors)
                        .setColumns(5)
                        .setRoundColorButton(true)
                        .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                            @Override
                            public void onChooseColor(int position, int color) {
                                s.edit().putString(getString(R.string.pref_key_color_a), String.format("#%06X", (0xFFFFFF & color))).apply();
                                colA.setBackgroundTintList(ColorStateList.valueOf(color));
                                colA.setTextColor(ColorUtils.isWhiteText(color) ? Color.WHITE : Color.BLACK);
                            }

                            @Override
                            public void onCancel() {}
                        }).show();
                } else {
                    Toast.makeText(colA.getContext(), "Cannot use color settings below android M", Toast.LENGTH_SHORT).show();
                }
            });
            final Button colB = view.findViewById(R.id.button_color_b);
            colB.setOnClickListener(v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final ColorPicker col = new ColorPicker(this.getActivity());
                Log.i("GSH:onActivityC", s.getString(getString(R.string.pref_key_color_b), getString(R.string.pref_default_color_b)));
                col.setDefaultColorButton(Color.parseColor(s.getString(getString(R.string.pref_key_color_b), getString(R.string.pref_default_color_b))))
                        .setColors(R.array.colors)
                        .setColumns(5)
                        .setRoundColorButton(true)
                        .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                            @Override
                            public void onChooseColor(int position, int color) {
                                s.edit().putString(getString(R.string.pref_key_color_b), String.format("#%06X", (0xFFFFFF & color))).apply();
                                colB.setBackgroundTintList(ColorStateList.valueOf(color));
                                colB.setTextColor(ColorUtils.isWhiteText(color) ? Color.WHITE : Color.BLACK);
                            }

                            @Override
                            public void onCancel() {}
                        }).show();
                } else {
                    Toast.makeText(colB.getContext(), "Cannot use color settings below android M", Toast.LENGTH_SHORT).show();
                }
            });
            final Button high = view.findViewById(R.id.button_highlight);
            high.setOnClickListener(v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final ColorPicker colo = new ColorPicker(this.getActivity());
                Log.i("GSH:onActivityC", s.getString(getString(R.string.pref_key_highlight), getString(R.string.pref_default_highlight)));
                colo.setDefaultColorButton(Color.parseColor(s.getString(getString(R.string.pref_key_highlight), getString(R.string.pref_default_highlight))))
                        .setColors(R.array.colors)
                        .setColumns(5)
                        .setRoundColorButton(true)
                        .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                            @Override
                            public void onChooseColor(int position, int color) {
                                s.edit().putString(getString(R.string.pref_key_highlight), String.format("#%06X", (0xFFFFFF & color))).apply();
                                high.setBackgroundTintList(ColorStateList.valueOf(color));
                                high.setTextColor(ColorUtils.isWhiteText(color) ? Color.WHITE : Color.BLACK);
                            }

                            @Override
                            public void onCancel() {}
                        }).show();
                } else {
                    Toast.makeText(high.getContext(), "Cannot use color settings below android M", Toast.LENGTH_SHORT).show();
                }
            });
            final Button border = view.findViewById(R.id.button_border);
            border.setOnClickListener(v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    final ColorPicker color = new ColorPicker(this.getActivity());
                    Log.i("GSH:onActivityC", s.getString(getString(R.string.pref_key_border), getString(R.string.pref_default_border)));
                    color.setDefaultColorButton(Color.parseColor(s.getString(getString(R.string.pref_key_border), getString(R.string.pref_default_border))))
                            .setColors(R.array.colors)
                            .setColumns(5)
                            .setRoundColorButton(true)
                            .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                                @Override
                                public void onChooseColor(int position, int color) {
                                    s.edit().putString(getString(R.string.pref_key_border), String.format("#%06X", (0xFFFFFF & color))).apply();
                                    border.setBackgroundTintList(ColorStateList.valueOf(color));
                                    border.setTextColor(ColorUtils.isWhiteText(color) ? Color.WHITE : Color.BLACK);
                                }

                                @Override
                                public void onCancel() {
                                }
                            }).show();
                } else {
                    Toast.makeText(border.getContext(), "Cannot use color settings below android M", Toast.LENGTH_SHORT).show();
                }
            });

            int cBlank = Color.parseColor(s.getString(getString(R.string.pref_key_blank), getString(R.string.pref_default_blank)));
            int cColA = Color.parseColor(s.getString(getString(R.string.pref_key_color_a), getString(R.string.pref_default_color_a)));
            int cColB = Color.parseColor(s.getString(getString(R.string.pref_key_color_b), getString(R.string.pref_default_color_b)));
            int cHigh = Color.parseColor(s.getString(getString(R.string.pref_key_highlight), getString(R.string.pref_default_highlight)));
            int cBorder = Color.parseColor(s.getString(getString(R.string.pref_key_border), getString(R.string.pref_default_border)));

            blank.setBackgroundTintList(ColorStateList.valueOf(cBlank));
            colA.setBackgroundTintList(ColorStateList.valueOf(cColA));
            colB.setBackgroundTintList(ColorStateList.valueOf(cColB));
            high.setBackgroundTintList(ColorStateList.valueOf(cHigh));
            border.setBackgroundTintList(ColorStateList.valueOf(cBorder));

            blank.setTextColor(ColorUtils.isWhiteText(cBlank) ? Color.WHITE : Color.BLACK);
            colA.setTextColor(ColorUtils.isWhiteText(cColA) ? Color.WHITE : Color.BLACK);
            colB.setTextColor(ColorUtils.isWhiteText(cColB) ? Color.WHITE : Color.BLACK);
            high.setTextColor(ColorUtils.isWhiteText(cHigh) ? Color.WHITE : Color.BLACK);
            border.setTextColor(ColorUtils.isWhiteText(cBorder) ? Color.WHITE : Color.BLACK);

            RadioGroup g = view.findViewById(R.id.size_group);
            int size = 3;
            Log.w("GSH:onActivityC", "Size before " + size);
            try {
                size = Integer.parseInt(s.getString(getString(R.string.pref_key_size), getString(R.string.pref_default_size)));
            } catch (NumberFormatException e) {
                Log.w("GSH:onActivityC", "Size isn't a number");
            }
            Log.w("GSH:onActivityC", "Size after " + size);
            switch (size) {
                case 4:
                    ((RadioButton) view.findViewById(R.id.radio_size_5)).setChecked(false);
                    ((RadioButton) view.findViewById(R.id.radio_size_6)).setChecked(false);
                    ((RadioButton) view.findViewById(R.id.radio_size_3)).setChecked(false);
                    ((RadioButton) view.findViewById(R.id.radio_size_4)).setChecked(true);
                    break;
                case 5:
                    ((RadioButton) view.findViewById(R.id.radio_size_6)).setChecked(false);
                    ((RadioButton) view.findViewById(R.id.radio_size_3)).setChecked(false);
                    ((RadioButton) view.findViewById(R.id.radio_size_4)).setChecked(false);
                    ((RadioButton) view.findViewById(R.id.radio_size_5)).setChecked(true);
                    break;
                case 6:
                    ((RadioButton) view.findViewById(R.id.radio_size_3)).setChecked(false);
                    ((RadioButton) view.findViewById(R.id.radio_size_4)).setChecked(false);
                    ((RadioButton) view.findViewById(R.id.radio_size_5)).setChecked(false);
                    ((RadioButton) view.findViewById(R.id.radio_size_6)).setChecked(true);
                    break;
                default:
                    ((RadioButton) view.findViewById(R.id.radio_size_4)).setChecked(false);
                    ((RadioButton) view.findViewById(R.id.radio_size_5)).setChecked(false);
                    ((RadioButton) view.findViewById(R.id.radio_size_6)).setChecked(false);
                    ((RadioButton) view.findViewById(R.id.radio_size_3)).setChecked(true);
                    break;
            }

            g.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId) {
                    case R.id.radio_size_4:
                        s.edit().putString(getString(R.string.pref_key_size), "4").apply();
                        break;
                    case R.id.radio_size_5:
                        s.edit().putString(getString(R.string.pref_key_size), "5").apply();
                        break;
                    case R.id.radio_size_6:
                        s.edit().putString(getString(R.string.pref_key_size), "6").apply();
                        break;
                    default:
                        s.edit().putString(getString(R.string.pref_key_size), "3").apply();
                        break;
                }
            });

            ImageView help = view.findViewById(R.id.button_help);

            View[] targets = new View[3];

            targets[0] = colB;
            targets[1] = g;
            targets[2] = b;

            String[] title = this.getResources().getStringArray(R.array.settings_screen_help_titles);
            String[] text = this.getResources().getStringArray(R.array.settings_screen_help_text);

            help.setOnClickListener(v -> {
                TapTargetSequence seq = new TapTargetSequence(this.getActivity());
                for (int i = 0; i < targets.length; i++) {
                    if (i == 1) {
                        seq.target(TapTarget.forView(targets[i], title[i], text[i])
                                .cancelable(true)
                                .transparentTarget(true)
                                .textColor(R.color.White)
                                .outerCircleColor(R.color.RoyalBlue)
                                .outerCircleAlpha(0.95F)
                                .targetRadius(5)
                                .drawShadow(true)
                                .dimColor(R.color.Black));
                    } else {
                        seq.target(TapTarget.forView(targets[i], title[i], text[i])
                                .cancelable(true)
                                .transparentTarget(true)
                                .textColor(R.color.White)
                                .outerCircleColor(R.color.RoyalBlue)
                                .outerCircleAlpha(0.95F)
                                .targetRadius(70)
                                .drawShadow(true)
                                .dimColor(R.color.Black));
                    }
                }
                seq.continueOnCancel(true).considerOuterCircleCanceled(true);
                seq.start();
            });
        }
    }

}
