package com.cjwsjy.app.vehicle;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.cjwsjy.app.R;

/**
 * aquery加载图片工具封装类
 *
 * @Copyright: Copyright (c)2013
 * @Company: 广州市道一信息有限公司
 * @author: Mr.y
 * @version: 1.0
 * @date: 2013-10-15 下午3:25:18
 * <p/>
 * Modification History:
 * Date         Author      Version     Description
 * ------------------------------------------------------------------
 * 2013-10-15      Mr.y          1.0       1.0 Version
 */
public class ImageViewTool {
    public static AQuery aq;
    public static Map<String, SoftReference<Bitmap>> mapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>();
    public static String imageCachePath = Environment.getExternalStorageDirectory()
			+ File.separator + "minaim"
			+ File.separator + "imageCache" + File.separator;
    
    
    public static void getAsyncImageSet(Object obj, String imgPath, int imgId) {
        ImageView imgView = null;
        if (obj instanceof Activity) {
            imgView = (ImageView) ((Activity) obj).findViewById(imgId);
        } else if (obj instanceof View) {
            imgView = (ImageView) ((View) obj).findViewById(imgId);
        }

        getAsyncImageSet(imgPath, imgView, R.drawable.ic_launcher);
    }

    public static void getAsyncImageSet(String imgPath, final ImageView imgView, int defaultImg) {
        try {

            if (null == aq) {
                aq = new AQuery(imgView.getContext());
            }
            //判断图片
            Bitmap bm = aq.getCachedImage(defaultImg);
            if (!RegUtil.isPictureUrl(imgPath)) {
                aq.id(imgView).image(bm);
                return;
            }

            if (imgView.getContext() != aq.getContext())
                aq = new AQuery(imgView.getContext());

            //绑定图片
//			aq.id(imgView).image(imgPath, true, true,bm.getWidth(), R.drawable.list_item_bg,bm,AQuery.FADE_IN,AQuery.RATIO_PRESERVE);
            aq.id(imgView).image(imgPath, true, true, 0, defaultImg, bm, 0, 31.0f / 40.0f);
        } catch (Exception e) {
        }
    }

    public static void getAsyncImageNormal(String imgPath, final ImageView imgView, int defaultImg) {
        try {

            if (null == aq) {
                aq = new AQuery(imgView.getContext());
            }
            Bitmap bm = aq.getCachedImage(defaultImg);
            if (!RegUtil.isPictureUrl(imgPath)) {
                aq.id(imgView).image(bm);
                return;
            }

            if (imgView.getContext() != aq.getContext())
                aq = new AQuery(imgView.getContext());

            //绑定图片
            aq.id(imgView).image(imgPath, true, true, 0, defaultImg, bm, 0, com.androidquery.util.Constants.RATIO_PRESERVE);
        } catch (Exception e) {
        }
    }
    
    public static void getAsyncImageBg2(String imgPath, final ImageView imgView, final int defaultImg, boolean getCache) {
    	if (null == aq){
			aq = new AQuery(imgView.getContext());
    	}
    	if (imgView.getContext() != aq.getContext())
			aq = new AQuery(imgView.getContext());

		aq.id(imgView).image(imgPath, getCache, getCache, 0, defaultImg, new BitmapAjaxCallback() {
			@Override
			protected void callback(String url, ImageView iv, final Bitmap bm, AjaxStatus status) {
			    setImage(url,iv, bm, defaultImg, -1,false ,status.getCode());
			}
		});
    }

    /**
     * 绑定背景图片
     *
     * @param imgPath    图片url
     * @param imgView    图片view
     * @param defaultImg 默认图片
     * @param getCache   是否取缓存
     */
    public static void getAsyncImageBg(String imgPath, final ImageView imgView, final int defaultImg, boolean getCache) {
        getAsyncImageBg(imgPath, imgView, defaultImg, getCache, -1, false);
    }

    public static void getAsyncImageBg(final String imgPath, final ImageView imgView, final int defaultImg, boolean getCache, final int roundX, final boolean shadow) {
		
		new Thread(){
			@Override
			public void run() {
		    	if (null == aq){
					aq = new AQuery(imgView.getContext());
		    	}
				final String imageName = imgPath.substring(imgPath.lastIndexOf("/") + 1, imgPath.length());
				final String imagePath = imageCachePath;
				final Bitmap bm;
		
				File file = new File(imagePath + imageName);
				System.out.println(imgPath);
		
				if (roundX == -99 && file.exists()) {
//					file.delete();
					handler.obtainMessage(0, file).sendToTarget();
				}else if (file.exists() && file.length() < 3072) {//小于3K的图片重新请求
//					file.delete();
					handler.obtainMessage(0, file).sendToTarget();
				} else if (roundX >= -1 && file.exists()) {
					bm = BitmapAjaxCallback.getResizedImage(imagePath + imageName,null, 0, true, 0);
					if (bm != null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								aq.id(imgView).image(toRoundBitmap(bm, roundX));
							}
						});
						return;
					} else {
//						file.delete();
						handler.obtainMessage(0, file).sendToTarget();
					}
				}
		
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (imgView.getContext() != aq.getContext())
							aq = new AQuery(imgView.getContext());
				
						aq.id(imgView).image(imgPath, false, false, 0, defaultImg, new BitmapAjaxCallback() {
							@Override
							protected void callback(String url, ImageView iv, final Bitmap bm, AjaxStatus status) {
							    setImage(url,iv, bm, defaultImg, roundX == -99 ? 0 : roundX, shadow,status.getCode());
							}
						});
					}
				});
				
			};
		}.start();
    }
    
    public static Handler handler = new Handler(){
    	@Override
		public void handleMessage(android.os.Message msg) {
    		if(msg.what == 0){
    			((File)msg.obj).delete();
    		}
    	};
    };
    
    public static void cacheImageFile(final Bitmap fbm,final String url){
    	handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					final String imageName = url.substring(url.lastIndexOf("/") + 1, url.length());
//					File iconFile = new File(imageCachePath+imageName);
					File iconFile = new File(SDCardUtil.getImageCacheDir(),imageName);
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(iconFile));
					fbm.compress(Bitmap.CompressFormat.PNG, 100, bos);
					bos.flush();
					bos.close();
//					fbm.recycle();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
    }

    static private void setImage(String url,ImageView iv, Bitmap bm, final int defaultImg, final int roundX, final boolean shadow,int code) {

        if (null != bm && bm.getHeight() > 10 && bm.getWidth() > 0) {
            iv.setVisibility(View.VISIBLE);
            Bitmap zoomBitMap = zoomBitmap(bm, iv.getLayoutParams().width, iv.getLayoutParams().height);
            if (roundX >= 0) {
                Bitmap bitmap = toRoundBitmap(zoomBitMap, roundX);
                zoomBitMap.recycle();
                if (shadow) {
                    Bitmap bitmap1 = drawImageDropShadow(bitmap);
                    iv.setImageDrawable(new BitmapDrawable(bitmap1));
//                    mapCache.put(cacheKey, new SoftReference<Bitmap>(bitmap1));
                    bitmap.recycle();
                } else {
                    iv.setImageDrawable(new BitmapDrawable(bitmap));
//                    mapCache.put(cacheKey, new SoftReference<Bitmap>(bitmap));
                }
            } else {
                if (shadow) {
                    Bitmap bitmap1 = drawImageDropShadow(zoomBitMap);
                    iv.setImageDrawable(new BitmapDrawable( bitmap1));
                    zoomBitMap.recycle();
//                    mapCache.put(cacheKey, new SoftReference<Bitmap>(bitmap1));
                } else {
                    iv.setImageDrawable(new BitmapDrawable( zoomBitMap));
//                    mapCache.put(cacheKey, new SoftReference<Bitmap>(zoomBitMap));
                }
            } 
//            String logo = "upload/personPhoto/" + BaseActivity.uservo.getUserId() + ".jpg";
//            if(url.contains(logo) && roundX==0 && !shadow){
//            	PersonUtil.isNotNeedUpdateLogo = true;
//            }
            if(bm != null && !bm.isRecycled() && code == 200){
            	System.out.println("添加缓存图片》》》》》");
            	cacheImageFile(bm, url);
            }
        } else {
            if (defaultImg > 0) {//当默认图片大于0，则设置默认图片，等于0则不做任何设置
                iv.setVisibility(View.VISIBLE);
                iv.setImageResource(defaultImg);
            } else if (defaultImg == -1) {//当默认图片为-1，表示需要隐藏该图片位置
                iv.setVisibility(View.GONE);
            }
        }
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    //绑定背景图片
    public static void getAsyncImageBgReal(String imgPath, final View imgView, final int defaultImg) {
        //判断图片
        /*if (Constants.isImgCache()||!RegUtil.isPictureUrl(imgPath)) {
            return ;
		}*/

        if (null == aq)
            aq = new AQuery(imgView.getContext());

        if (imgView.getContext() != aq.getContext())
            aq = new AQuery(imgView.getContext());

        aq.id(imgView).image(imgPath, true, true, 0, defaultImg, new BitmapAjaxCallback() {
            @Override
            protected void callback(String url, ImageView iv, Bitmap bm,
                                    AjaxStatus status) {
                if (null != bm && bm.getHeight() > 10 && bm.getWidth() > 0)
                    iv.setBackgroundDrawable(new BitmapDrawable(bm));
                else
                    iv.setBackgroundResource(defaultImg);
            }
        });
    }


    /**
     *  * 转换图片成圆形
     *  * @param bitmap 传入Bitmap对象
     *  * @return
     *  
     */
    static public Bitmap toRoundBitmap(Bitmap bitmap, int roundPx) {
        if (roundPx < 0 || bitmap == null)
            return bitmap;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = roundPx == 0 ? width / 2 : (roundPx);
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = roundPx == 0 ? height / 2 : roundPx;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        try {
            Bitmap output = Bitmap.createBitmap(width,
                    height, Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
            final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
            final RectF rectF = new RectF(dst);

            paint.setAntiAlias(true);

            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, src, dst, paint);
//            bitmap.recycle();
//            output.recycle();

            return output;
        } catch (Exception e) {
            return bitmap;
        }
    }


//    static public Bitmap drawImageDropShadow(Bitmap originalBitmap){
//        BlurMaskFilter blurFilter = new BlurMaskFilter(3, BlurMaskFilter.Blur.OUTER);
//        Paint shadowPaint = new Paint();
//        shadowPaint.setMaskFilter(blurFilter);
//        int[] offsetXY =new int[2];
//        Bitmap shadowBitmap = originalBitmap.extractAlpha(shadowPaint,offsetXY);
//        Bitmap shadowImage32 = shadowBitmap.copy(Bitmap.Config.ARGB_8888, true);
//        Canvas c = new Canvas(shadowImage32);
//        c.drawBitmap(originalBitmap, 0,0, null);
//       return originalBitmap;
//    }

    private static Bitmap drawImageDropShadow(Bitmap originalBitmap) {
        float radius = 1.5f;
        float readiusHalf = radius / 2;
//        	EmbossMaskFilter filter = new EmbossMaskFilter(new float[]{ 0, 0, 1 }, 0.1f, 20, 20.0f);
        BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
        Paint shadowPaint = new Paint();
        shadowPaint.setAlpha(200);
//        shadowPaint.setStyle(Paint.Style.FILL);
//        shadowPaint.setColor(Color.WHITE);
//        shadowPaint.setAntiAlias(true);
        shadowPaint.setMaskFilter(filter);
        int[] offsetXY = new int[2];
        Bitmap shadowBitmap = originalBitmap.extractAlpha(shadowPaint, offsetXY);
        Bitmap shadowBitmap32 = shadowBitmap.copy(Config.ARGB_8888, true);
        Canvas c = new Canvas(shadowBitmap32);
//        Log.d("flydy", " px:"+offsetXY[0]+" py:"+offsetXY[1]);
        c.drawBitmap(originalBitmap, 0, 0, null);
        shadowBitmap.recycle();
//        shadowBitmap32.recycle();
//        originalBitmap.recycle();
        return shadowBitmap32;
    }
    
    
    
    /**
     * 用于显示图片activity
     * @param imgPath
     * @param imgView
     * @param defaultImg
     */
  	public static void asynForShowImage(String imgPath,final ImageView imgView,final int defaultImg){
  		if(null==aq)
  			aq = new AQuery(imgView.getContext());
  		
  		if(aq.getCachedImage(imgPath) != null){
  			int w = aq.getCachedImage(imgPath).getWidth() > Constants.SCR_WIDTH ? Constants.SCR_WIDTH : aq.getCachedImage(imgPath).getWidth();
  			int h = aq.getCachedImage(imgPath).getHeight() > Constants.SCR_HEIGHT ? Constants.SCR_HEIGHT : aq.getCachedImage(imgPath).getHeight();
  			imgView.setLayoutParams(new LinearLayout.LayoutParams(w,h));
  			imgView.setImageDrawable(new BitmapDrawable(aq.getCachedImage(imgPath)));
  			
  			return;
  		}
  		
  		if(imgView.getContext()!=aq.getContext())
  			aq = new AQuery(imgView.getContext());
  		
  		aq.id(imgView).image(imgPath, true, true, 0, defaultImg, new BitmapAjaxCallback(){     
  			@Override
  			protected void callback(String url, ImageView iv, Bitmap bm,AjaxStatus status) {
  				if(null != bm && bm.getHeight() > 10 && bm.getWidth() > 0){
  					iv.setVisibility(View.VISIBLE);
  					int w = bm.getWidth() > Constants.SCR_WIDTH ? Constants.SCR_WIDTH : bm.getWidth();
  		  			int h = bm.getHeight() > Constants.SCR_HEIGHT ? Constants.SCR_HEIGHT : bm.getHeight();
  					iv.setLayoutParams(new LinearLayout.LayoutParams(w,h));
  					iv.setImageDrawable(new BitmapDrawable(bm));
  				}else{
  					if(defaultImg > 0){//当默认图片大于0，则设置默认图片，等于0则不做任何设置
  						iv.setVisibility(View.VISIBLE);
  						int w = bm.getWidth() > Constants.SCR_WIDTH ? Constants.SCR_WIDTH : bm.getWidth();
  	  		  			int h = bm.getHeight() > Constants.SCR_HEIGHT ? Constants.SCR_HEIGHT : bm.getHeight();
  						//iv.setLayoutParams(new Gallery.LayoutParams(w,h));
  						iv.setImageResource(defaultImg);
  					}else if(defaultImg == -1){//当默认图片为-1，表示需要隐藏该图片位置
  						iv.setVisibility(View.GONE);
  					}
  				}
  			}
  		});
  	}
    /**
     * 用于显示图片activity
     * @param imgPath
     * @param imgView
     * @param defaultImg
     */
  	public static void asynForShowImage2(String imgPath,final ImageView imgView,final int defaultImg){
  		if(null==aq)
  			aq = new AQuery(imgView.getContext());
  		
  		if(aq.getCachedImage(imgPath) != null){
  			int w = aq.getCachedImage(imgPath).getWidth() > Constants.SCR_WIDTH ? Constants.SCR_WIDTH : aq.getCachedImage(imgPath).getWidth();
  			int h = aq.getCachedImage(imgPath).getHeight() > Constants.SCR_HEIGHT ? Constants.SCR_HEIGHT : aq.getCachedImage(imgPath).getHeight();
  			imgView.setImageDrawable(new BitmapDrawable(aq.getCachedImage(imgPath)));
  			
  			return;
  		}
  		
  		if(imgView.getContext()!=aq.getContext())
  			aq = new AQuery(imgView.getContext());
  		
  		aq.id(imgView).image(imgPath, true, true, 0, defaultImg, new BitmapAjaxCallback(){     
  			@Override
  			protected void callback(String url, ImageView iv, Bitmap bm,AjaxStatus status) {
  				if(null != bm && bm.getHeight() > 10 && bm.getWidth() > 0){
  					iv.setVisibility(View.VISIBLE);
  					int w = bm.getWidth() > Constants.SCR_WIDTH ? Constants.SCR_WIDTH : bm.getWidth();
  		  			int h = bm.getHeight() > Constants.SCR_HEIGHT ? Constants.SCR_HEIGHT : bm.getHeight();
  					iv.setImageDrawable(new BitmapDrawable(bm));
  				}else{
  					if(defaultImg > 0){//当默认图片大于0，则设置默认图片，等于0则不做任何设置
  						iv.setVisibility(View.VISIBLE);
  						int w = bm.getWidth() > Constants.SCR_WIDTH ? Constants.SCR_WIDTH : bm.getWidth();
  	  		  			int h = bm.getHeight() > Constants.SCR_HEIGHT ? Constants.SCR_HEIGHT : bm.getHeight();
  						iv.setImageResource(defaultImg);
  					}else if(defaultImg == -1){//当默认图片为-1，表示需要隐藏该图片位置
  						iv.setVisibility(View.GONE);
  					}
  				}
  			}
  		});
  	}
}
