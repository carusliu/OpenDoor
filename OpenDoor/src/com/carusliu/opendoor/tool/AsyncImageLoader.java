package com.carusliu.opendoor.tool;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

public class AsyncImageLoader {
	//private HashMap<String, SoftReference<Bitmap>> imageCache;  
	private ImageFileCache fileCache; 
	private ImageMemoryCache memoryCache;  
    private Context context;
    public AsyncImageLoader(Context context) {  
    	this.context = context;
        //imageCache = new HashMap<String, SoftReference<Bitmap>>();  
        fileCache=new ImageFileCache();  
        memoryCache=new ImageMemoryCache(context);  
    }  
   
    public Bitmap loadBitmap(final String imageUrl, final ImageCallback imageCallback) {
    	//先从内存中获取
    	Bitmap result = memoryCache.getBitmapFromCache(imageUrl);  
        if (result == null) {  
            // 文件缓存中获取  
            result = fileCache.getImage(imageUrl);  
            if(result!=null){
	            // 添加到内存缓存  
	            memoryCache.addBitmapToCache(imageUrl, result);  
            }
        }  
       
        final Handler handler = new Handler() {  
            public void handleMessage(Message message) { 
            	//图片获取完成，重新加载图片
                imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);  
            }  
        }; 
        
        
        //异步加载图片
        new Thread() {  
            @Override  
            public void run() {  
                Bitmap bitmap = loadImageFromUrl(imageUrl);
                if(bitmap==null){
                	return;
                }
                memoryCache.addBitmapToCache(imageUrl, bitmap);  
                fileCache.saveBitmap(bitmap, imageUrl);  
                Message message = handler.obtainMessage(0, bitmap);  
                handler.sendMessage(message);  
            }  
        }.start();  
        //图片加载完成前先返回null，Adapter会设置成默认图片
        return result;  
    }  
   
   public static Bitmap loadImageFromUrl(String imageUrl) {  
	   InputStream imageStream = null;
	   
	   try {
			imageStream = new URL(imageUrl).openConnection().getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;   
		options.inPurgeable = true;  
		options.inInputShareable = true;  
		options.inSampleSize = 2;
		Bitmap bitmap = BitmapFactory.decodeStream(imageStream,null,options);
       return bitmap;  
   }  
   
    public interface ImageCallback {  
        public void imageLoaded(Bitmap imageBitmap, String imageUrl);  
    }  
    
  //判断是否有网络连接
	public boolean isOnline() {
	    ConnectivityManager connMgr = (ConnectivityManager) 
	            context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    return (networkInfo != null && networkInfo.isConnected());
	} 
}
