package com.afollestad.aesthetic;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

import android.annotation.TargetApi;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v4.widget.EdgeEffectCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;
import android.widget.EdgeEffect;
import android.widget.ScrollView;
import com.simplecity.amp_library.BuildConfig;
import java.lang.reflect.Field;

@RestrictTo(LIBRARY_GROUP)
final class EdgeGlowUtil {

  // Prevent instantiation
  private EdgeGlowUtil() {}

  // Reflection field holders (lowerCamelCase)
  private static Field edgeGlowFieldEdge;
  private static Field edgeGlowFieldGlow;
  private static Field edgeEffectCompatFieldEdgeEffect;
  private static Field scrollViewFieldEdgeGlowTop;
  private static Field scrollViewFieldEdgeGlowBottom;
  private static Field nestedScrollViewFieldEdgeGlowTop;
  private static Field nestedScrollViewFieldEdgeGlowBottom;
  private static Field listViewFieldEdgeGlowTop;
  private static Field listViewFieldEdgeGlowBottom;
  private static Field recyclerViewFieldEdgeGlowTop;
  private static Field recyclerViewFieldEdgeGlowLeft;
  private static Field recyclerViewFieldEdgeGlowRight;
  private static Field recyclerViewFieldEdgeGlowBottom;
  private static Field viewPagerFieldEdgeGlowLeft;
  private static Field viewPagerFieldEdgeGlowRight;

  // Repeated field-name literals pulled into constants
  private static final String FIELD_EDGE               = "mEdge";
  private static final String FIELD_GLOW               = "mGlow";
  private static final String FIELD_EDGE_EFFECT        = "mEdgeEffect";
  private static final String FIELD_EDGE_GLOW_TOP      = "mEdgeGlowTop";
  private static final String FIELD_EDGE_GLOW_BOTTOM   = "mEdgeGlowBottom";
  private static final String FIELD_TOP_GLOW           = "mTopGlow";
  private static final String FIELD_BOTTOM_GLOW        = "mBottomGlow";
  private static final String FIELD_LEFT_GLOW          = "mLeftGlow";
  private static final String FIELD_RIGHT_GLOW         = "mRightGlow";
  private static final String FIELD_LEFT_EDGE          = "mLeftEdge";
  private static final String FIELD_RIGHT_EDGE         = "mRightEdge";

  private static void invalidateEdgeEffectFields() {
    if (edgeGlowFieldEdge != null
        && edgeGlowFieldGlow != null
        && edgeEffectCompatFieldEdgeEffect != null) {
      edgeGlowFieldEdge.setAccessible(true);
      edgeGlowFieldGlow.setAccessible(true);
      edgeEffectCompatFieldEdgeEffect.setAccessible(true);
      return;
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      Field edge = null;
      Field glow = null;
      for (Field f : EdgeEffect.class.getDeclaredFields()) {
        String name = f.getName();
        if (FIELD_EDGE.equals(name)) {
          f.setAccessible(true);
          edge = f;
        } else if (FIELD_GLOW.equals(name)) {
          f.setAccessible(true);
          glow = f;
        }
      }
      edgeGlowFieldEdge = edge;
      edgeGlowFieldGlow = glow;
    } else {
      edgeGlowFieldEdge = null;
      edgeGlowFieldGlow = null;
    }

    Field efc = null;
    try {
      efc = EdgeEffectCompat.class.getDeclaredField(FIELD_EDGE_EFFECT);
      efc.setAccessible(true);
    } catch (NoSuchFieldException ignored) {
      if (BuildConfig.DEBUG) ignored.printStackTrace();
    }
    edgeEffectCompatFieldEdgeEffect = efc;
  }

  private static void invalidateScrollViewFields() {
    if (scrollViewFieldEdgeGlowTop != null && scrollViewFieldEdgeGlowBottom != null) {
      scrollViewFieldEdgeGlowTop.setAccessible(true);
      scrollViewFieldEdgeGlowBottom.setAccessible(true);
      return;
    }
    final Class<?> cls = ScrollView.class;
    for (Field f : cls.getDeclaredFields()) {
      String name = f.getName();
      if (FIELD_EDGE_GLOW_TOP.equals(name)) {
        f.setAccessible(true);
        scrollViewFieldEdgeGlowTop = f;
      } else if (FIELD_EDGE_GLOW_BOTTOM.equals(name)) {
        f.setAccessible(true);
        scrollViewFieldEdgeGlowBottom = f;
      }
    }
  }

  private static void invalidateNestedScrollViewFields() {
    if (nestedScrollViewFieldEdgeGlowTop != null
        && nestedScrollViewFieldEdgeGlowBottom != null) {
      nestedScrollViewFieldEdgeGlowTop.setAccessible(true);
      nestedScrollViewFieldEdgeGlowBottom.setAccessible(true);
      return;
    }
    final Class<?> cls = NestedScrollView.class;
    for (Field f : cls.getDeclaredFields()) {
      String name = f.getName();
      if (FIELD_EDGE_GLOW_TOP.equals(name)) {
        f.setAccessible(true);
        nestedScrollViewFieldEdgeGlowTop = f;
      } else if (FIELD_EDGE_GLOW_BOTTOM.equals(name)) {
        f.setAccessible(true);
        nestedScrollViewFieldEdgeGlowBottom = f;
      }
    }
  }

  private static void invalidateListViewFields() {
    if (listViewFieldEdgeGlowTop != null && listViewFieldEdgeGlowBottom != null) {
      listViewFieldEdgeGlowTop.setAccessible(true);
      listViewFieldEdgeGlowBottom.setAccessible(true);
      return;
    }
    final Class<?> cls = AbsListView.class;
    for (Field f : cls.getDeclaredFields()) {
      String name = f.getName();
      if (FIELD_EDGE_GLOW_TOP.equals(name)) {
        f.setAccessible(true);
        listViewFieldEdgeGlowTop = f;
      } else if (FIELD_EDGE_GLOW_BOTTOM.equals(name)) {
        f.setAccessible(true);
        listViewFieldEdgeGlowBottom = f;
      }
    }
  }

  private static void invalidateRecyclerViewFields() {
    if (recyclerViewFieldEdgeGlowTop != null
        && recyclerViewFieldEdgeGlowLeft != null
        && recyclerViewFieldEdgeGlowRight != null
        && recyclerViewFieldEdgeGlowBottom != null) {
      recyclerViewFieldEdgeGlowTop.setAccessible(true);
      recyclerViewFieldEdgeGlowLeft.setAccessible(true);
      recyclerViewFieldEdgeGlowRight.setAccessible(true);
      recyclerViewFieldEdgeGlowBottom.setAccessible(true);
      return;
    }
    final Class<?> cls = RecyclerView.class;
    for (Field f : cls.getDeclaredFields()) {
      String name = f.getName();
      if (FIELD_TOP_GLOW.equals(name)) {
        f.setAccessible(true);
        recyclerViewFieldEdgeGlowTop = f;
      } else if (FIELD_BOTTOM_GLOW.equals(name)) {
        f.setAccessible(true);
        recyclerViewFieldEdgeGlowBottom = f;
      } else if (FIELD_LEFT_GLOW.equals(name)) {
        f.setAccessible(true);
        recyclerViewFieldEdgeGlowLeft = f;
      } else if (FIELD_RIGHT_GLOW.equals(name)) {
        f.setAccessible(true);
        recyclerViewFieldEdgeGlowRight = f;
      }
    }
  }

  private static void invalidateViewPagerFields() {
    if (viewPagerFieldEdgeGlowLeft != null && viewPagerFieldEdgeGlowRight != null) {
      viewPagerFieldEdgeGlowLeft.setAccessible(true);
      viewPagerFieldEdgeGlowRight.setAccessible(true);
      return;
    }
    final Class<?> cls = ViewPager.class;
    for (Field f : cls.getDeclaredFields()) {
      String name = f.getName();
      if (FIELD_LEFT_EDGE.equals(name)) {
        f.setAccessible(true);
        viewPagerFieldEdgeGlowLeft = f;
      } else if (FIELD_RIGHT_EDGE.equals(name)) {
        f.setAccessible(true);
        viewPagerFieldEdgeGlowRight = f;
      }
    }
  }

  static void setEdgeGlowColor(@NonNull ScrollView scrollView, @ColorInt int color) {
    invalidateScrollViewFields();
    try {
      Object ee = scrollViewFieldEdgeGlowTop.get(scrollView);
      setEffectColor(ee, color);
      ee = scrollViewFieldEdgeGlowBottom.get(scrollView);
      setEffectColor(ee, color);
    } catch (Exception ex) {
      if (BuildConfig.DEBUG) ex.printStackTrace();
    }
  }

  static void setEdgeGlowColor(
      @NonNull NestedScrollView scrollView, @ColorInt int color) {
    invalidateNestedScrollViewFields();
    try {
      Object ee = nestedScrollViewFieldEdgeGlowTop.get(scrollView);
      setEffectColor(ee, color);
      ee = nestedScrollViewFieldEdgeGlowBottom.get(scrollView);
      setEffectColor(ee, color);
    } catch (Exception ex) {
      if (BuildConfig.DEBUG) ex.printStackTrace();
    }
  }

  static void setEdgeGlowColor(@NonNull AbsListView listView, @ColorInt int color) {
    invalidateListViewFields();
    try {
      Object ee = listViewFieldEdgeGlowTop.get(listView);
      setEffectColor(ee, color);
      ee = listViewFieldEdgeGlowBottom.get(listView);
      setEffectColor(ee, color);
    } catch (Exception ex) {
      if (BuildConfig.DEBUG) ex.printStackTrace();
    }
  }

  static void setEdgeGlowColor(
      @NonNull RecyclerView recyclerView,
      @ColorInt int color,
      @Nullable RecyclerView.OnScrollListener scrollListener) {
    invalidateRecyclerViewFields();
    if (scrollListener == null) {
      scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(
            RecyclerView rv, int newState) {
          super.onScrollStateChanged(rv, newState);
          EdgeGlowUtil.setEdgeGlowColor(rv, color, this);
        }
      };
      recyclerView.addOnScrollListener(scrollListener);
    }
    try {
      Object ee = recyclerViewFieldEdgeGlowTop.get(recyclerView);
      setEffectColor(ee, color);
      ee = recyclerViewFieldEdgeGlowBottom.get(recyclerView);
      setEffectColor(ee, color);
      ee = recyclerViewFieldEdgeGlowLeft.get(recyclerView);
      setEffectColor(ee, color);
      ee = recyclerViewFieldEdgeGlowRight.get(recyclerView);
      setEffectColor(ee, color);
    } catch (Exception ex) {
      if (BuildConfig.DEBUG) ex.printStackTrace();
    }
  }

  static void setEdgeGlowColor(@NonNull ViewPager pager, @ColorInt int color) {
    invalidateViewPagerFields();
    try {
      Object ee = viewPagerFieldEdgeGlowLeft.get(pager);
      setEffectColor(ee, color);
      ee = viewPagerFieldEdgeGlowRight.get(pager);
      setEffectColor(ee, color);
    } catch (Exception ex) {
      if (BuildConfig.DEBUG) ex.printStackTrace();
    }
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private static void setEffectColor(Object edgeEffect, @ColorInt int color) {
    invalidateEdgeEffectFields();
    if (edgeEffect instanceof EdgeEffectCompat) {
      try {
        edgeEffect = edgeEffectCompatFieldEdgeEffect.get(edgeEffect);
      } catch (IllegalAccessException e) {
        if (BuildConfig.DEBUG) e.printStackTrace();
        return;
      }
    }
    if (edgeEffect == null) {
      return;
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      try {
        Drawable edge = (Drawable) edgeGlowFieldEdge.get(edgeEffect);
        Drawable glow = (Drawable) edgeGlowFieldGlow.get(edgeEffect);
        edge.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        glow.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        edge.setCallback(null);
        glow.setCallback(null);
      } catch (Exception ex) {
        if (BuildConfig.DEBUG) ex.printStackTrace();
      }
    } else {
      ((EdgeEffect) edgeEffect).setColor(color);
    }
  }
}
