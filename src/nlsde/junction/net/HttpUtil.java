package nlsde.junction.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.net.ssl.KeyStoreBuilderParameters;

import nlsde.junction.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class HttpUtil {

	// 锟斤拷锟斤拷HttpClient锟斤拷锟斤拷
	public static  HttpParams httpParams = new BasicHttpParams(); 

	public static  HttpClient httpClient = new DefaultHttpClient(httpParams);
	public static final String BASE_URL = "http://115.28.247.105:8040/userAction/user/";
	 
	/**
	 * @param url
	 *            锟斤拷锟斤拷锟斤拷锟斤拷锟経RL
	 * @return 锟斤拷锟斤拷锟斤拷锟斤拷应锟街凤拷
	 * @throws Exception
	 * 
	 * 
	 * @author bingoc
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static String getRequest(final String url) throws Exception {
		FutureTask<String> task = new FutureTask<String>(
				new Callable<String>() {
					@Override
					public String call() throws Exception {
						HttpGet get = new HttpGet(url);// 锟斤拷锟斤拷httpget锟斤拷锟斤拷
						HttpResponse httpResponse = httpClient.execute(get);// 锟斤拷锟斤拷锟斤拷锟斤拷
						HttpConnectionParams.setConnectionTimeout(httpParams, 2000);
						HttpConnectionParams.setSoTimeout(httpParams, 2000);
						httpClient = new DefaultHttpClient(httpParams);
						if (httpResponse.getStatusLine().getStatusCode() == 200) {
							String resultString = EntityUtils
									.toString(httpResponse.getEntity());// 锟斤拷取锟斤拷锟斤拷锟斤拷锟斤拷应锟街凤拷
							return resultString;
						}
						return null;
					}

				});
		new Thread(task).start();
		
			return task.get();
		
	}
	/**
	 * @param url
	 *            锟斤拷锟斤拷锟斤拷锟斤拷锟経RL
	 * @param params
	 *            锟斤拷锟斤拷锟斤拷锟?
	 * @return 锟斤拷锟斤拷锟斤拷锟斤拷应锟街凤拷
	 * @throws Exception
	 */
	public static String postRequest(final String url,final Map<String, String> rawParams)throws Exception
	{
		FutureTask<String > task =new FutureTask<String>(
				new Callable<String>() {

					@Override
					public String call() throws Exception {
						HttpPost post =new  HttpPost(url);
						List<NameValuePair> paramslList=new ArrayList<NameValuePair>();
						for(String key:rawParams.keySet())
						{
							paramslList.add(new BasicNameValuePair(key, rawParams.get(key)	));//锟斤拷装锟斤拷锟斤拷锟斤拷锟?
						}
						post.setEntity(new UrlEncodedFormEntity(paramslList,"utf-8"));
						HttpConnectionParams.setConnectionTimeout(httpParams, 2000);
						HttpConnectionParams.setSoTimeout(httpParams, 2000);
						httpClient = new DefaultHttpClient(httpParams);
						HttpResponse httpResponse =httpClient.execute(post);
						if(httpResponse.getStatusLine().getStatusCode()==200)
						{
							String result=EntityUtils.toString(httpResponse.getEntity());
							
							//JSONObject jsonObject = new JSONObject(result);
						
							return result;
						}
						else {
							
						}
						return null;
					}
					
				});
		new Thread(task).start();
		return task.get();
	}
}
