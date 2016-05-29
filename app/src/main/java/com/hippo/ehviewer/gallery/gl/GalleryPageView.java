/*
 * Copyright 2016 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hippo.ehviewer.gallery.gl;

import android.content.Context;

import com.hippo.glview.glrenderer.BasicTexture;
import com.hippo.glview.glrenderer.Texture;
import com.hippo.glview.image.GLImageMovableTextView;
import com.hippo.glview.image.ImageMovableTextTexture;
import com.hippo.glview.image.ImageTexture;
import com.hippo.glview.view.Gravity;
import com.hippo.glview.widget.GLFrameLayout;
import com.hippo.glview.widget.GLLinearLayout;
import com.hippo.glview.widget.GLMovableTextView;
import com.hippo.glview.widget.GLProgressView;
import com.hippo.glview.widget.GLTextureView;
import com.hippo.yorozuya.LayoutUtils;

public class GalleryPageView extends GLFrameLayout {

    public static final int INVALID_INDEX = -1;

    private static final int INFO_INTERVAL = 24;
    public static final float PROGRESS_GONE = -1.0f;
    public static final float PROGRESS_INDETERMINATE = -2.0f;
    private static final int PAGE_MIN_HEIGHT = 256;

    private final ImageView mImage;
    private final GLLinearLayout mInfo;
    private final GLImageMovableTextView mPage;
    private final GLTextureView mError;
    private final GLProgressView mProgress;

    private final int mPageMinHeight;

    private int mIndex = INVALID_INDEX;

    public GalleryPageView(Context context, ImageMovableTextTexture pageTextTexture,
            int progressColor, int progressSize) {
        // Add image
        mImage = new ImageView();
        GravityLayoutParams glp = new GravityLayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        addComponent(mImage, glp);

        // Add other panel
        mInfo = new GLLinearLayout();
        mInfo.setOrientation(GLLinearLayout.VERTICAL);
        mInfo.setInterval(LayoutUtils.dp2pix(context, INFO_INTERVAL));
        glp = new GravityLayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        glp.gravity = Gravity.CENTER;
        addComponent(mInfo, glp);

        // Add page
        mPage = new GLImageMovableTextView();
        mPage.setTextTexture(pageTextTexture);
        GLLinearLayout.LayoutParams lp = new GLLinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        mInfo.addComponent(mPage, lp);

        // Add error
        mError = new GLTextureView();
        lp = new GLLinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        mInfo.addComponent(mError, lp);

        // Add progress
        mProgress = new GLProgressView();
        mProgress.setBgColor(GalleryView.BACKGROUND_COLOR);
        mProgress.setColor(progressColor);
        mProgress.setMinimumWidth(progressSize);
        mProgress.setMinimumHeight(progressSize);
        lp = new GLLinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        mInfo.addComponent(mProgress, lp);

        mPageMinHeight = LayoutUtils.dp2pix(context, PAGE_MIN_HEIGHT);
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        // The height of the actual image may be smaller than mPageMinHeight.
        // Set min height as 0 when the image is visible.
        // For PageLayoutManager, min height is useless.
        if (mImage.getVisibility() == VISIBLE) {
            return 0;
        } else {
            return mPageMinHeight;
        }
    }

    int getIndex() {
        return mIndex;
    }

    void setIndex(int index) {
        mIndex = index;
    }

    public void showImage() {
        mImage.setVisibility(VISIBLE);
        mInfo.setVisibility(GONE);
    }

    public void showInfo() {
        // For image valid rect
        mImage.setVisibility(INVISIBLE);
        mInfo.setVisibility(VISIBLE);
    }

    private void unbindImage() {
        ImageTexture texture = mImage.getImageTexture();
        if (texture != null) {
            mImage.setImageTexture(null);
            texture.recycle();
        }
    }

    public void setImage(ImageTexture imageTexture) {
        unbindImage();
        if (imageTexture != null) {
            mImage.setImageTexture(imageTexture);
        }
    }

    public void setPage(int page) {
        mPage.setText(Integer.toString(page));
    }

    public void setProgress(float progress) {
        if (progress == PROGRESS_GONE) {
            mProgress.setVisibility(GONE);
        } else if (progress == PROGRESS_INDETERMINATE) {
            mProgress.setVisibility(VISIBLE);
            mProgress.setIndeterminate(true);
        } else {
            mProgress.setVisibility(VISIBLE);
            mProgress.setIndeterminate(false);
            mProgress.setProgress(progress);
        }
    }

    private void unbindError() {
        Texture texture = mError.getTexture();
        if (texture != null) {
            mError.setTexture(null);
            if (texture instanceof BasicTexture) {
                ((BasicTexture) texture).recycle();
            }
        }
    }

    public void setError(String error, GalleryView galleryView) {
        unbindError();
        if (error == null) {
            mError.setVisibility(GONE);
        } else {
            mError.setVisibility(VISIBLE);
            galleryView.bindErrorView(mError, error);
        }
    }

    public ImageView getImageView() {
        return mImage;
    }

    public boolean isLoaded() {
        return mImage.getVisibility() == VISIBLE;
    }
}
