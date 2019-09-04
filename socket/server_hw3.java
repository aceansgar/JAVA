package java_hw3;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.*;
import java.io.*;
import com.hankcs.hanlp.seg.*;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.seg.NShort.NShortSegment;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.DenseInstance;

public class Server_hw3 
{
	private static String test_server_address="222.29.98.46";
	private static int hw3_port=6789;
	private static int test_server_port=9876;
	private static String student_id="000000";

	public static void main(String args[])
	{
		
		try 
		{
//			remote_get_score();
			remote_init_test();
			
			ServerSocket hw3_server = new ServerSocket(6789);
			
			while (true) 
			{
//				System.out.println("aaa");
				Socket test_server = hw3_server.accept();
				System.out.println("a package received!");
				new HW3_CallManager(test_server,student_id).start();
			}
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	static void remote_init_test()
	{
		try 
		{
			
			// remote call init_test
			Hashtable<String, Object> msg = new Hashtable<String, Object>();
			msg.put("method", "initTest");
			msg.put("param1",student_id);
			msg.put("param2", "UTF-8");
			msg.put("id",student_id);
//			System.out.println(msg);
			Socket test_server_caller = new Socket(test_server_address, test_server_port);
			ObjectOutputStream os = new ObjectOutputStream(test_server_caller.getOutputStream());
			os.writeObject(msg);
			os.flush();
			test_server_caller.close();
			System.out.println("end of remote call initTest");

			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	static void remote_get_score()
	{

		try
		{
			Hashtable<String, Object> msg = new Hashtable<String, Object>();
			msg.put("method", "getScore");
			msg.put("id",student_id);
			msg.put("param1",student_id);
			Socket test_server_caller = new Socket(test_server_address, test_server_port);
			ObjectOutputStream os = new ObjectOutputStream(test_server_caller.getOutputStream());
			os.writeObject(msg);
			os.flush();
			test_server_caller.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}

class HW3_CallManager extends Thread
{
	private Socket test_server;
	private String student_id;
	private String question_id;
	private int test_server_port=9876;
	HW3_CallManager(Socket _test_server,String _id)
	{
		this.test_server=_test_server;
		this.student_id=_id;
	}
	
	public void run() 
	{
		try 
		{
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(test_server.getInputStream()));
			Hashtable<String, Object> msg = (Hashtable<String, Object>)is.readObject();
			String method = (String)msg.get("method");
			System.out.println("Msg from " + test_server.getInetAddress()+": "+method);
			String result = "";
			if(method.equals("test"))
			{
				ArrayList<String> trainingset0=(ArrayList<String>)msg.get("param1");
				ArrayList<String> trainingset1=(ArrayList<String>)msg.get("param2");
				ArrayList<String> trainingset2=(ArrayList<String>)msg.get("param3");
				ArrayList<String> testingset=(ArrayList<String>)msg.get("param4");
				this.question_id=(String)msg.get("id");
				result=test(trainingset0,trainingset1,trainingset2,testingset);
//				System.out.println(result);
			}
			else
			{
				String res_str="response of "+method+":\n";
				String call_res=(String)msg.get("result");
				String student_id=(String)msg.get("id");
				res_str+=call_res+"\n";
				res_str+=student_id+"\n";
				System.out.println(res_str);
			}

//			msg.clear();
//			msg.put("method", method);
//			msg.put("result", result);
//			msg.put("id", student_id);
//
//			System.out.println("Sending result to " + test_server.getInetAddress() + ":" + msg.toString());
//
//			Socket caller = new Socket(test_server.getInetAddress(), 9876);
//			ObjectOutputStream os = new ObjectOutputStream(caller.getOutputStream());
//			os.writeObject(msg);
//			os.flush();
//			caller.close();
		}
		catch (Exception e) 
		{
			System.err.println(e.getMessage());
		}
	}
	
	String test(ArrayList<String> trainingset0,ArrayList<String> trainingset1,
			ArrayList<String> trainingset2,ArrayList<String> testingset)
	{
		try
		{
			int trainingset_len=20;
			int testingset_len=180;
			if(trainingset0.size()!=trainingset_len)
			{
				return "error in trainingset0";
			}
			if(trainingset1.size()!=trainingset_len)
			{
				return "error in trainingset1";
			}
			if(trainingset2.size()!=trainingset_len)
			{
				return "error in trainingset2";
			}
			if(testingset.size()!=testingset_len)
			{
				return "error in testingset";
			}
//			System.out.println("format of data is appropriate");
			int[] label_predict_list=weka_predict(trainingset0,trainingset1,trainingset2,testingset);
//			List<Integer> ans=Arrays.stream(label_predict_list).boxed().collect(Collectors.toList());
			ArrayList<Integer> ans=new ArrayList<Integer>();
			for(int test_id=0;test_id<testingset_len;++test_id)
			{
				ans.add(label_predict_list[test_id]);
			}
			Hashtable<String, Object> msg = new Hashtable<String, Object>();
			msg.put("method", "submit");
			msg.put("id",student_id);
			msg.put("param1",student_id);
			msg.put("param2", question_id);
			msg.put("param3",ans);
			
			System.out.println("submit question: "+question_id);
			
//			System.out.println("submit message");
//			System.out.println("ans len: "+String.valueOf(ans.size()));
//			System.out.println((List)ans);
			
			

			Socket caller = new Socket(this.test_server.getInetAddress(), this.test_server_port);
			ObjectOutputStream os = new ObjectOutputStream(caller.getOutputStream());
			os.writeObject(msg);
			os.flush();
			caller.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return "success";
	}
	
	int[] weka_predict(ArrayList<String> trainingset0,ArrayList<String> trainingset1,
			ArrayList<String> trainingset2,ArrayList<String> testingset)
	{
		
		
		String[] feature_words = 
			{"之", "其", "或", "亦", "方", "于", "即", "皆", "因", "仍", "故", "尚", "呢", "了",
			"的", "着", "一", "不", "乃", "呀", "吗", "咧", "啊", "吧", "让", "向", "往", "是",
			"在", "越", "再", "更", "比", "很", "偏", "别", "好", "可", "便", "就", "但", "儿",
			"又", "也", "都", "要", "这", "那", "你", "我", "他", "来", "去", "道", "说"
			};
		int feature_num=feature_words.length;
		
		int train_size_0=trainingset0.size();
		int train_size_1=trainingset1.size();
		int train_size_2=trainingset2.size();
		int tot_train_size=train_size_0+train_size_1+train_size_2;
		int test_size=testingset.size();
		
		int[] label_predict_list=new int[test_size];
		
		int[][] all_train_feature=new int[tot_train_size][feature_num];
		int[][] all_test_feature=new int[test_size][feature_num];
		int[] train_label=new int[tot_train_size];
		
		construct_train_feature_label(all_train_feature,train_label,feature_words,
				trainingset0,trainingset1,trainingset2);
		construct_test_feature(all_test_feature,feature_words,testingset);
		
		ArrayList<Attribute> attributes=new ArrayList<Attribute>();
		for(int feature_id=0;feature_id<feature_num;++feature_id)
		{
			String attr_name="word_"+String.valueOf(feature_id);
			Attribute tmp_attr=new Attribute(attr_name);
			attributes.add(tmp_attr);
		}
		List<String> tag_list = Arrays.asList("0", "1", "2");
		Attribute tag_attr=new Attribute("tag",tag_list);
		attributes.add(tag_attr);
		
		Instances train_instances=new Instances("train_instances",attributes,tot_train_size);
		train_instances.setClassIndex(train_instances.numAttributes() - 1);
		for(int train_id=0;train_id<tot_train_size;++train_id)
		{
			Instance instance = new DenseInstance(attributes.size());
			for(int feature_id=0;feature_id<feature_num;++feature_id)
			{
//				String attr_name="word_"+String.valueOf(feature_id);
				instance.setValue(feature_id,all_train_feature[train_id][feature_id]);
			}
			instance.setValue(tag_attr,String.valueOf(train_label[train_id]));
			train_instances.add(instance);
		}
		
		Instances test_instances=new Instances("test_instances",attributes,test_size);
		test_instances.setClassIndex(test_instances.numAttributes() - 1);
		for(int test_id=0;test_id<test_size;++test_id)
		{
			Instance instance = new DenseInstance(attributes.size());
			for(int feature_id=0;feature_id<feature_num;++feature_id)
			{
				instance.setValue(feature_id,all_test_feature[test_id][feature_id]);
			}
			// no tag
			test_instances.add(instance);
		}
		try
		{
			Classifier classifier = (Classifier)Class.forName("weka.classifiers.trees.RandomForest").newInstance();
	        classifier.buildClassifier(train_instances);
			for (int test_id = 0; test_id < test_size; ++test_id) 
			{
				Instance tmp_instance=test_instances.instance(test_id);
//				System.out.println()
				Double temp_label = classifier.classifyInstance(tmp_instance);
				label_predict_list[test_id] = temp_label.intValue();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		

		return label_predict_list;
	}
	
	void construct_train_feature_label(int[][] _all_train_feature,int[] _train_label,String[] _feature_words,
			ArrayList<String> trainingset0,ArrayList<String> trainingset1,
			ArrayList<String> trainingset2)
	{
		
		int train_size_0=trainingset0.size();
		int train_size_1=trainingset1.size();
		int train_size_2=trainingset2.size();
		int tot_train_size=train_size_0+train_size_1+train_size_2;
		int feature_num=_feature_words.length;
		
		for(int id=0;id<train_size_0;++id)
		{
			int train_id=id;
			String train_text=trainingset0.get(id);
			int[] feature_cnt_list=calc_words(_feature_words,train_text);
			for(int feature_id=0;feature_id<feature_num;++feature_id)
			{
				_all_train_feature[train_id][feature_id]=feature_cnt_list[feature_id];
			}
			_train_label[train_id]=0;
		}
		
		for(int id=0;id<train_size_1;++id)
		{
			int train_id=id+train_size_0;
			String train_text=trainingset1.get(id);
			int[] feature_cnt_list=calc_words(_feature_words,train_text);
			for(int feature_id=0;feature_id<feature_num;++feature_id)
			{
				_all_train_feature[train_id][feature_id]=feature_cnt_list[feature_id];
			}
			_train_label[train_id]=1;
		}
		
		for(int id=0;id<train_size_2;++id)
		{
			int train_id=id+train_size_0+train_size_1;
			String train_text=trainingset2.get(id);
			int[] feature_cnt_list=calc_words(_feature_words,train_text);
			for(int feature_id=0;feature_id<feature_num;++feature_id)
			{
				_all_train_feature[train_id][feature_id]=feature_cnt_list[feature_id];
			}
			_train_label[train_id]=2;
		}
		

	}
	
	void construct_test_feature(int[][] _all_test_feature,String[] _feature_words,
			ArrayList<String> testingset)
	{
		int feature_num=_feature_words.length;
		int test_size=testingset.size();
		for(int test_id=0;test_id<test_size;++test_id)
		{
			String test_text=testingset.get(test_id);
			int[] feature_cnt_list=calc_words(_feature_words,test_text);
			for(int feature_id=0;feature_id<feature_num;++feature_id)
			{
				_all_test_feature[test_id][feature_id]=feature_cnt_list[feature_id];
			}
		}
	}
	
	int[] calc_words(String[] _feature_words,String _text_str)
	{

        Segment segment = new NShortSegment();
        int feature_num=_feature_words.length;
        int[] feature_cnt_list=new int[feature_num];
        for(int i=0;i<feature_num;++i)
        	feature_cnt_list[i]=0;
		try
		{
            List<Term> termList = null;
            termList = segment.seg(_text_str);
            //for each word in the chapter, see if it is a feature word
            // if it is a feature word, add 1 to this feature
			for (int i = 0; i < termList.size(); i++) 
			{
                String tmp_word = termList.get(i).word;
				for (int j = 0; j < feature_num; j++)
					if (tmp_word.equals(_feature_words[j])) 
					{
						feature_cnt_list[j]++;
						break;
					}
            } 
        } 
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return feature_cnt_list;
		
	}
	

	
}
