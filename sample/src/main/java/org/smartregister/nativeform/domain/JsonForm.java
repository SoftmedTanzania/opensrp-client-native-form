package org.smartregister.nativeform.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.nativeform.contract.FormTesterContract;
import org.smartregister.nativeform.util.FileReaderUtil;

import java.io.File;

import timber.log.Timber;


/***
 * This is an Immutable class.
 * The class constructor is heavy and should be created in a back ground thread
 */
public class JsonForm implements FormTesterContract.NativeForm {

    private String formName;
    private String fileName;
    private boolean isValid = false;
    private JSONObject jsonObject;
    private Form form = null;

    @WorkerThread
    public JsonForm(@NonNull File sourceFile, Form form) {
        fileName = sourceFile.getName();
        this.form = form;
        try {
            jsonObject = new JSONObject(FileReaderUtil.getStringFromFile(sourceFile));
            if (jsonObject.has(JsonFormConstants.ENCOUNTER_TYPE)) {
                formName = jsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE);
                isValid = true;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Nullable
    @Override
    public String getFormName() {
        return formName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Nullable
    @Override
    public JSONObject getJsonForm() {
        return jsonObject;
    }

    @Nullable
    @Override
    public Form getFormDetails() {
        return form;
    }
}
