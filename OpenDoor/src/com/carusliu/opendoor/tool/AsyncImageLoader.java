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
    	//�ȴ��ڴ��л�ȡ
    	Bitmap result = memoryCache.getBitmapFromCache(imageUrl);  
        if (result == null) {  
            // �ļ������л�ȡ  
            result = fileCache.getImage(imageUrl);  
            if(result!=null){
	            // ��ӵ��ڴ滺��  
	            memoryCache.addBitmapToCache(imageUrl, result);  
            }
        }  
       
        final Handler handler = new Handler() {  
            public void handleMessage(Message message) { 
            	//ͼƬ��ȡ��ɣ����¼���ͼƬ
                imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);  
            }  
        }; 
        
        
        //�첽����ͼƬ
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
        //ͼƬ�������ǰ�ȷ���null��Adapter�����ó�Ĭ��ͼƬ
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
    
  //�ж��Ƿ�����������
	public boolean isOnline() {
	    ConnectivityManager connMgr = (ConnectivityManager) 
	            context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    return (networkInfo != null && networkInfo.isConnected());
	} 
}
