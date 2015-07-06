/*
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.projecttango.experiments.javaarealearning;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * This Class shows a dialog to set the name of an ADF. When you press okay
 * SetNameLocation Call back is called where setting the name should be handled.
 */
public class GoDialog extends DialogFragment implements OnClickListener {
    private EditText mNameEditText;
    private GoCommunicator mCommunicator;
    private String adfUuid;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCommunicator = (GoCommunicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflator.inflate(R.layout.set_name_dialog, null);
        getDialog().setTitle(R.string.go_dialogTitle);
        mNameEditText = (EditText) dialogView.findViewById(R.id.name);
        dialogView.findViewById(R.id.Ok).setOnClickListener(this);
        dialogView.findViewById(R.id.cancel).setOnClickListener(this);
        setCancelable(false);
        return dialogView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Ok:
                mCommunicator.onGo(mNameEditText.getText().toString());
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }

    interface GoCommunicator {
        public void onGo(String destinationName);
    }
}
