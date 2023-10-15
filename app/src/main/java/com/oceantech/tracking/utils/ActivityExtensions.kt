/*
 * Designed and developed by 2020 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oceantech.tracking.utils

import android.content.Context
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.oceantech.tracking.R

/** get a material container arc transform. */
internal fun getContentTransform(context: Context): MaterialContainerTransform {
  // Tạo và cấu hình một MaterialContainerTransform, một loại hiệu ứng chuyển đổi từ Material Design.
  return MaterialContainerTransform().apply {
    // Xác định mục tiêu của hiệu ứng chuyển đổi. Trong trường hợp này, nó sẽ là nội dung chính của màn hình (android.R.id.content).
    addTarget(android.R.id.content)

    // Đặt thời gian của hiệu ứng (450 ms).
    duration = 450

    // Sử dụng MaterialArcMotion để xác định đường di chuyển của hiệu ứng.
    pathMotion = MaterialArcMotion()

    // Bật hiển thị bóng đổ khi chuyển đổi.
    isElevationShadowEnabled = true

    // Đặt độ nâng cao ban đầu và kết thúc cho thành phần chuyển đổi.
    startElevation = 9f
    endElevation = 9f

    // Đặt màu nền ban đầu cho thành phần chuyển đổi dựa trên tài nguyên colorPrimary.
    startContainerColor = ContextCompat.getColor(context, R.color.blue)
  }
}

/** apply material exit container transformation. */
fun AppCompatActivity.applyExitMaterialTransform() {
  // Yêu cầu tính năng chuyển đổi cho cửa sổ.
  window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)

  // Đặt một hồi gọi chia sẻ phần tử ra khỏi màn hình bằng cách sử dụng MaterialContainerTransformSharedElementCallback.
  setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())

  // Tắt việc sử dụng overlay cho các phần tử chia sẻ.
  window.sharedElementsUseOverlay = false
}

/** apply material entered container transformation. */
fun AppCompatActivity.applyMaterialTransform(transitionName: String?) {
  // Yêu cầu tính năng chuyển đổi cho cửa sổ.
  window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)

  // Đặt tên chuyển đổi cho phần tử chia sẻ bằng cách sử dụng giá trị transitionName.
  ViewCompat.setTransitionName(findViewById(android.R.id.content), transitionName)

  // Đặt hồi gọi chia sẻ phần tử khi chuyển đổi vào màn hình sử dụng MaterialContainerTransformSharedElementCallback.
  setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())

  // Đặt chuyển đổi chia sẻ phần tử khi vào màn hình và khi quay lại màn hình trước đó bằng cách sử dụng getContentTransform(this).
  window.sharedElementEnterTransition = getContentTransform(this)
  window.sharedElementReturnTransition = getContentTransform(this)
}

