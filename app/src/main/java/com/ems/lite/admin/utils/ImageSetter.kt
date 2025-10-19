package com.ems.lite.admin.utils

import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import java.io.File

object ImageSetter {
    enum class LoadImageType {
        ROUND, NONE
    }

    fun loadImage(mUrl: String?, placeHolder: Int, mImageView: ImageView) {
        if (!mUrl.isNullOrEmpty()) {
            Picasso.get().load(mUrl)
                .placeholder(placeHolder)
                .into(mImageView)
        } else {
            mImageView.setImageResource(placeHolder)
        }
    }

    fun loadImage(file: File?, placeHolder: Int, mImageView: ImageView): Boolean {
        return if (file != null) {
            Picasso.get().load(file)
                .placeholder(placeHolder)
                .into(mImageView)
            true
        } else {
            mImageView.setImageResource(placeHolder)
            false
        }
    }

    /* fun loadImageResize(
         mUrl: String?, placeHolder: Int, mImageView: ImageView, width: Int, height: Int
     ): Boolean {
         return if (!mUrl.isNullOrEmpty()) {
             Picasso.get().load(mUrl)
                 .placeholder(placeHolder)
                 .resize(width, height)
                 .into(mImageView)
 //            mImageView.load(mUrl) {
 //                transformations(CircleCropTransformation())
 //                size(width, height)
 //                placeholder(placeHolder)
 //            }
             true
         } else {
             mImageView.setImageResource(placeHolder)
             false
         }
     }*/

    fun loadRoundedImage(mUrl: String?, placeHolder: Int, mImageView: ImageView) {
        if (!mUrl.isNullOrEmpty()) {
            Picasso.get().load(mUrl)
                .transform(CircleTransform())
                .placeholder(placeHolder)
                .error(placeHolder)
                .into(mImageView)
        } else {
            mImageView.setImageResource(placeHolder)
        }
    }

    /*fun loadRoundedImage(file: File?, placeHolder: Int, mImageView: ImageView): Boolean {
        return if (file != null) {
            Picasso.get().load(file)
                .transform(CircleTransform())
                .placeholder(placeHolder)
                .error(placeHolder)
                .into(mImageView)
            true
        } else {
            mImageView.setImageResource(placeHolder)
            false
        }
    }*/

    /*fun loadRoundedImageCenterCrop(
        mUrl: String?, placeHolder: Int, mImageView: ImageView,
        width: Int, height: Int
    ) {
        if (!mUrl.isNullOrEmpty()) {
            Picasso.get().load(mUrl)
                .resize(width, height)
                .centerCrop()
                .transform(CircleTransform())
                .placeholder(placeHolder)
                .error(placeHolder)
                .into(mImageView)
        } else {
            mImageView.setImageResource(placeHolder)
        }
    }


    fun loadRoundedImageCenterCrop(
        mUrl: File?, placeHolder: Int, mImageView: ImageView, width: Int, height: Int
    ) {
        if (mUrl != null) {
            Picasso.get().load(mUrl)
                .resize(width, height)
                .centerCrop()
                .placeholder(placeHolder)
                .transform(CircleTransform())
                .into(mImageView)
        } else {
            mImageView.setImageResource(placeHolder)
        }
    }


    fun loadRoundedCornerImageResize(
        mUrl: String?, placeHolder: Int, radius: Int, margin: Int, mImageView: ImageView,
        width: Int, height: Int
    ) {
        if (!mUrl.isNullOrEmpty()) {
            val transformation: Transformation = RoundedCornersTransformation(
                radius, margin, RoundedCornersTransformation.CornerType.ALL
            )
            Picasso.get().load(mUrl)
                .resize(width, height)
                .transform(transformation)
                .placeholder(placeHolder)
                .error(placeHolder)
                .into(mImageView)
        } else {
            mImageView.setImageResource(placeHolder)
        }
    }

    fun loadRoundedCornerImageResize(
        file: File?, placeHolder: Int, radius: Int, margin: Int, mImageView: ImageView,
        width: Int, height: Int
    ) {
        if (file != null) {
            val transformation: Transformation = RoundedCornersTransformation(
                radius, margin, RoundedCornersTransformation.CornerType.ALL
            )
            Picasso.get().load(file)
                .resize(width, height)
                .transform(transformation)
                .placeholder(placeHolder)
                .error(placeHolder)
                .into(mImageView)
        } else {
            mImageView.setImageResource(placeHolder)
        }
    }*/

    fun loadRoundedCornerImage(
        url: String?, placeHolder: Int, radius: Int, margin: Int, mImageView: ImageView,
    ) {
        if (!url.isNullOrEmpty()) {
            val transformation: Transformation = RoundedCornersTransformation(
                radius, margin, RoundedCornersTransformation.CornerType.ALL
            )
            Picasso.get().load(url)
                .transform(transformation)
                .placeholder(placeHolder)
                .error(placeHolder)
                .into(mImageView)
        } else {
            mImageView.setImageResource(placeHolder)
        }
    }
}
