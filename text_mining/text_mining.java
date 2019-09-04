package hw2;

import java.util.List;
import java.util.Random;
import java.util.*;
import java.io.*;
import com.hankcs.hanlp.seg.*;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.seg.NShort.NShortSegment;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

// category 0: hlm 1-80
// category 1: hlm 81-120
// category 2: sgyy
// 20 in category 0
// 20 in category 1
// 40 in category 2


public class text_mining
{
	public static int category_size_0=80;
	public static int category_size_1=40;
	public static int category_size_2=120;
	
	
	//train size of each category
	public static int train_size_0=20;
	public static int train_size_1=20;
	public static int train_size_2=40;
	
	public static String hlm_path="./data/hlm/";
	public static String sgyy_path="./data/sgyy/";
	
	public static int[][] all_train_feature;
	public static int[][] all_test_feature;
	public static int[] train_label;
	
	public static void generate(String _train_list_path,String _test_list_path,
			String _test_benchmark_path)
	{
		FileWriter train_list_file=null;
		FileWriter test_list_file=null;
		FileWriter test_benchmark_file=null;
		try
		{
			train_list_file=new FileWriter(_train_list_path);
			test_list_file=new FileWriter(_test_list_path);
			test_benchmark_file=new FileWriter(_test_benchmark_path);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		
		Random rand=new Random();
		
		//select train data and test data from category 0
		ArrayList<Integer> train_list_0=new ArrayList<Integer>();
		int cnt_train_list_0=0;
		while(cnt_train_list_0<train_size_0)
		{
			int tmp_rand=rand.nextInt(category_size_0);
			int tmp_file_id=tmp_rand;
			if(!train_list_0.contains(tmp_file_id))
			{
				cnt_train_list_0++;
				train_list_0.add(tmp_file_id);
			}	
		}
		if(cnt_train_list_0!=train_size_0)
			System.err.println("cnt_train_list_0!=train_size_0\n");
		Iterator<Integer> itr_0=train_list_0.iterator();
		while(itr_0.hasNext())
		{
			int tmp_file_id=itr_0.next();//0-79
			int path_arg_0=tmp_file_id/10;
			int path_arg_1=tmp_file_id-path_arg_0*10;
			++path_arg_0;
			++path_arg_1;
			String tmp_path=hlm_path+"hlm0"+String.valueOf(path_arg_0)+"_"
					+String.valueOf(path_arg_1)+".txt";
			String tmp_line="0|"+tmp_path+"\n";
			try
			{
				train_list_file.write(tmp_line);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		for(int i=0;i<category_size_0;++i)
		{
			if(train_list_0.contains(i))
				continue;
			// it is a test chapter
			int tmp_file_id=i;//0-79
			int path_arg_0=tmp_file_id/10;
			int path_arg_1=tmp_file_id-path_arg_0*10;
			++path_arg_0;
			++path_arg_1;
			String tmp_path=hlm_path+"hlm0"+String.valueOf(path_arg_0)+"_"
					+String.valueOf(path_arg_1)+".txt";
			String tmp_line=tmp_path+"\n";
			try
			{
				test_list_file.write(tmp_line);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			tmp_line="0\n";
			try
			{
				test_benchmark_file.write(tmp_line);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	
		//select train data and test data from category 1
		//hlm 81-120
		ArrayList<Integer> train_list_1=new ArrayList<Integer>();
		int cnt_train_list_1=0;
		while(cnt_train_list_1<train_size_1)
		{
			int tmp_rand=rand.nextInt(category_size_1);
			int tmp_file_id=tmp_rand;
			if(!train_list_1.contains(tmp_file_id))
			{
				cnt_train_list_1++;
				train_list_1.add(tmp_file_id);
			}	
		}
		if(cnt_train_list_1!=train_size_1)
			System.err.println("cnt_train_list_1!=train_size_1\n");
		Iterator<Integer> itr_1=train_list_1.iterator();
		while(itr_1.hasNext())
		{
			int tmp_file_id=itr_1.next();//0-39
			tmp_file_id=tmp_file_id+80;//80-119
			int path_arg_0=tmp_file_id/10;
			int path_arg_1=tmp_file_id-path_arg_0*10;
			++path_arg_0;
			++path_arg_1;
			String tmp_path;
			if(path_arg_0<10)
				tmp_path=hlm_path+"hlm0"+String.valueOf(path_arg_0)+"_"
						+String.valueOf(path_arg_1)+".txt";
			else
				tmp_path=hlm_path+"hlm"+String.valueOf(path_arg_0)+"_"
						+String.valueOf(path_arg_1)+".txt";
			String tmp_line="1|"+tmp_path+"\n";
			try
			{
				train_list_file.write(tmp_line);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		for(int i=0;i<category_size_1;++i)
		{
			if(train_list_1.contains(i))
				continue;
			// it is a test chapter
			int tmp_file_id=i+80;//80-119
			int path_arg_0=tmp_file_id/10;
			int path_arg_1=tmp_file_id-path_arg_0*10;
			++path_arg_0;
			++path_arg_1;
			String tmp_path;
			if(path_arg_0<10)
				tmp_path=hlm_path+"hlm0"+String.valueOf(path_arg_0)+"_"
						+String.valueOf(path_arg_1)+".txt";
			else
				tmp_path=hlm_path+"hlm"+String.valueOf(path_arg_0)+"_"
						+String.valueOf(path_arg_1)+".txt";
			String tmp_line=tmp_path+"\n";
			try
			{
				test_list_file.write(tmp_line);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			tmp_line="1\n";
			try
			{
				test_benchmark_file.write(tmp_line);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		//select train data and test data from category 2
		ArrayList<Integer> train_list_2=new ArrayList<Integer>();
		int cnt_train_list_2=0;
		while(cnt_train_list_2<train_size_2)
		{
			int tmp_rand=rand.nextInt(category_size_2);
			int tmp_file_id=tmp_rand;
			if(!train_list_2.contains(tmp_file_id))
			{
				cnt_train_list_2++;
				train_list_2.add(tmp_file_id);
			}	
		}
		if(cnt_train_list_2!=train_size_2)
			System.err.println("cnt_train_list_2!=train_size_2\n");
		Iterator<Integer> itr_2=train_list_2.iterator();
		while(itr_2.hasNext())
		{
			int tmp_file_id=itr_2.next();//0-119
			int path_arg_0=tmp_file_id/10;
			int path_arg_1=tmp_file_id-path_arg_0*10;
			++path_arg_0;
			++path_arg_1;
			String tmp_path;
			if(path_arg_0<10)
				tmp_path=sgyy_path+"sgyy0"+String.valueOf(path_arg_0)+"_"
					+String.valueOf(path_arg_1)+".txt";
			else
				tmp_path=sgyy_path+"sgyy"+String.valueOf(path_arg_0)+"_"
						+String.valueOf(path_arg_1)+".txt";
			String tmp_line="2|"+tmp_path+"\n";
			try
			{
				train_list_file.write(tmp_line);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		for(int i=0;i<category_size_2;++i)
		{
			if(train_list_2.contains(i))
				continue;
			// it is a test chapter
			int tmp_file_id=i;//0-119
			int path_arg_0=tmp_file_id/10;
			int path_arg_1=tmp_file_id-path_arg_0*10;
			++path_arg_0;
			++path_arg_1;
			String tmp_path;
			if(path_arg_0<10)
				tmp_path=sgyy_path+"sgyy0"+String.valueOf(path_arg_0)+"_"
					+String.valueOf(path_arg_1)+".txt";
			else
				tmp_path=sgyy_path+"sgyy"+String.valueOf(path_arg_0)+"_"
						+String.valueOf(path_arg_1)+".txt";
			String tmp_line=tmp_path+"\n";
			try
			{
				test_list_file.write(tmp_line);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			tmp_line="2\n";
			try
			{
				test_benchmark_file.write(tmp_line);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		System.out.println("generate end\n");
	
		try
		{
			train_list_file.flush();
			test_list_file.flush();
			test_benchmark_file.flush();
			train_list_file.close();
			test_list_file.close();
			test_benchmark_file.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void init_global(int feature_num)
	{
		int train_size=train_size_0+train_size_1+train_size_2;
		all_train_feature=new int[train_size][feature_num];
		int test_size=category_size_0+category_size_1+category_size_2-train_size;
		all_test_feature=new int[test_size][feature_num];
		train_label=new int[train_size];
	}
	
	public static int[] calc_words(String chapter_path,String[] feature_words) 
	{
		int feature_num=feature_words.length;
        int[] feature_cnt_list = new int[feature_num];
        for (int i = 0; i < feature_num; ++i)
			feature_cnt_list[i] = 0;
		
        BufferedReader br = null;
		
		try 
		{
			InputStreamReader isr=new InputStreamReader(new FileInputStream(chapter_path),"UTF-8");
            br = new BufferedReader(isr);
		}
		catch (Exception e)
		{
            System.err.println("Fail to read datafile " + chapter_path );
            e.printStackTrace();
        }
		
        Segment segment = new NShortSegment();
		try
		{
            List<Term> termList = null;
            String str = null;
			do 
			{
                str = br.readLine();
                if(str==null)
                	break;
                termList = segment.seg(str);
                //for each word in the chapter, see if it is a feature word
                // if it is a feature word, add 1 to this feature
				for (int i = 0; i < termList.size(); i++) 
				{
                    String tmp = termList.get(i).word;
					for (int j = 0; j < feature_num; j++)
						if (tmp.equals(feature_words[j])) 
						{
							feature_cnt_list[j]++;
							break;
						}
                }
            }while (str != null);
        } 
		catch(Exception e)
		{
        	e.printStackTrace();
		}
        return feature_cnt_list;
    }
	
	public static void construct_train_feature(String _train_list_path,String[] _feature_words) 
	{
		BufferedReader br = null;
		try 
		{
            br = new BufferedReader( new FileReader (_train_list_path));
		} 
		catch (FileNotFoundException e)
		{
            e.printStackTrace();
        }
		try
		{
			String tmp_line = null;
			
			
			tmp_line = br.readLine();
			int train_chapter_cnt=0;
			while (tmp_line != null) 
			{
				char label_char = tmp_line.charAt(0);
				int label = (int)(label_char - '0');
				
				String train_chapter_path = tmp_line.substring(2);
				
				int[] feature_cnt = calc_words(train_chapter_path,_feature_words);
				
				int feature_num=_feature_words.length;
				for (int i = 0; i <feature_num ; i++)
					all_train_feature[train_chapter_cnt][i] = feature_cnt[i];
				train_label[train_chapter_cnt] = label;
				train_chapter_cnt++;
				
				tmp_line = br.readLine();
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void construct_test_feature(String _test_list_path,String[] _feature_words) 
	{
		BufferedReader br = null;
        try 
        {
            br = new BufferedReader( new FileReader (_test_list_path));
        } 
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        try
        {
			String tmp_line =null;
			
			int test_chapter_cnt=0;
			do 
			{
				tmp_line = br.readLine();
				if(tmp_line==null)
					break;
				String test_chapter_path=tmp_line;
				
				int[] feature_cnt = calc_words(test_chapter_path,_feature_words);
				int feature_num=_feature_words.length;
				for (int i = 0; i < feature_num ; i++)
					all_test_feature[test_chapter_cnt][i] = feature_cnt[i];
				test_chapter_cnt++;
				
			} while (tmp_line != null);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void create_train_arff(String _arff_path,String[] _feature_words)
	{
        FileWriter file_writer = null;
		try 
		{
            file_writer = new FileWriter(_arff_path);
            file_writer.write("@relation train_feature_label\n");
            int feature_num=_feature_words.length;
            for (int i = 0; i < feature_num ; i++)
            {
            	String tmp_line="@attribute word_"+String.valueOf(i)+" real\n";
                file_writer.write(tmp_line);
            }
            file_writer.write("@attribute category {0,1,2}\n");
            file_writer.write("@data\n");
            int train_chapter_cnt=all_train_feature.length;
			for (int l = 0; l < train_chapter_cnt; l++)
			{
				for (int feature_id=0; feature_id < feature_num; feature_id++)
				{
                    file_writer.write(all_train_feature[l][feature_id]+" ");
                }
                file_writer.write(String.valueOf(train_label[l]));
                file_writer.write("\n");
            }
            file_writer.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    }
	
	public static void create_test_arff(String _arff_path,String[] feature_words)
	{
        FileWriter file_writer = null;
		try 
		{
            file_writer = new FileWriter(_arff_path);
            file_writer.write("@relation guss_kind\r\n");
            int feature_num=feature_words.length;
            for (int i = 0; i < feature_num; i++)
            {
            	String tmp_line="@attribute word_"+String.valueOf(i)+" real\n";
                file_writer.write(tmp_line);
            }
            file_writer.write("@attribute category {0,1,2}\n");
            file_writer.write("@data\n");
            int test_chapter_cnt=all_test_feature.length;
            for (int l = 0; l < test_chapter_cnt; l++)
            {
                for (int i = 0; i < feature_num; i++)
                {
                    file_writer.write(all_test_feature[l][i]+" ");
                }
                // no label
                file_writer.write("?");
                file_writer.write("\n");
            }
            file_writer.close();
        }
		catch(Exception e)
		{
        	e.printStackTrace();
		}
    }
	
	public static int[] classify_test_data(String _train_arff,String _test_arff,int _test_cnt) 
	{
		int[] predict_test_label=new int[_test_cnt];
		try 
		{
			File train_file = new File(_train_arff);
			ArffLoader train_loader = new ArffLoader();
			train_loader.setFile(train_file);
			Instances inst1 = train_loader.getDataSet();
			// this index is label
			inst1.setClassIndex(inst1.numAttributes()-1);
			
			Classifier cl = (Classifier)Class.forName("weka.classifiers.trees.RandomForest").newInstance();
            cl.buildClassifier(inst1);
			
			File test_file = new File(_test_arff);
			ArffLoader test_loader = new ArffLoader();
			test_loader.setFile(test_file);
			Instances inst2 = test_loader.getDataSet();
			// this label is the goal
			inst2.setClassIndex(inst2.numAttributes()-1);
			

			for (int i = 0; i < _test_cnt; i++) 
			{
				Double temp_label = cl.classifyInstance(inst2.instance(i));
				predict_test_label[i] = temp_label.intValue();
			}
			
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		return predict_test_label;
	}
	
	public static void store_result(int[] _predict_test_label,String res_path,int _test_cnt) 
	{
		FileWriter file_writer = null;
		try 
		{
			file_writer = new FileWriter(res_path);
			for (int i = 0; i < _test_cnt; i++)
				file_writer.write(_predict_test_label[i]+"\n");
			file_writer.close();
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void print_accuracy(int[] _predict_test_label,String _test_benchmark_path,
			int _test_cnt) 
	{
		BufferedReader br = null;
        try 
        {
            br = new BufferedReader( new FileReader (_test_benchmark_path));
        } 
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
		
        int[] benchmark_test_label=new int[_test_cnt];
        int tmp_id=0;
        String tmp_line=null;
        while(true)
        {
        	try
        	{
        		tmp_line=br.readLine();
        	}
        	catch(Exception e)
        	{
        		e.printStackTrace();
        	}
        	if(tmp_line==null)
        		break;   
        	int tmp_benchmark_label=(int)(tmp_line.charAt(0)-'0');
        	benchmark_test_label[tmp_id]=tmp_benchmark_label;
        	++tmp_id;
        }
        
		int match_cnt = 0;
		for (int i = 0; i < _test_cnt; i++)
		{
			int label_benchmark=benchmark_test_label[i];
			int label_predict=_predict_test_label[i];
			if (benchmark_test_label[i] == _predict_test_label[i]) 
				match_cnt++;
		}
		System.out.println("accuracy: "+ String.valueOf(match_cnt) + " of " + _test_cnt);
	}
	
	public static void main(String[] args)
	{
		if(args.length==1&&args[0].equals("gen"))
		{
			// randomly generate training set, test set, test result
			String train_list_path="./data/train_list.txt";
			String test_list_path="./data/test_list.txt";
			String test_benchmark_path="./data/test_benchmark.txt";
			generate(train_list_path,test_list_path,test_benchmark_path);	
		}
		else if(args.length==1&&args[0].equals("test"))
		{
			String train_list_path="./data/train_list.txt";
			String test_list_path="./data/test_list.txt";
			String test_benchmark_path="./data/test_benchmark.txt";
			String[] feature_words = 
					{"之", "其", "或", "亦", "方", "于", "即", "皆", "因", "仍", "故", "尚", "呢", "了",
					"的", "着", "一", "不", "乃", "呀", "吗", "咧", "啊", "吧", "让", "向", "往", "是",
					"在", "越", "再", "更", "比", "很", "偏", "别", "好", "可", "便", "就", "但", "儿",
					"又", "也", "都", "要", "这", "那", "你", "我", "他", "来", "去", "道", "说"
					};
			String train_arff_path="train.arff";
			String test_arff_path="test.arff";
			String res_path="HW2_1600012819.txt";
			
			int feature_num=feature_words.length;
			init_global(feature_num);
			construct_train_feature(train_list_path,feature_words);
			construct_test_feature(test_list_path,feature_words);
			create_train_arff(train_arff_path,feature_words);
			create_test_arff(test_arff_path,feature_words);
			int test_cnt=category_size_0+category_size_1+category_size_2
					-train_size_0-train_size_1-train_size_2;
			int[] predict_test_label=classify_test_data(train_arff_path,test_arff_path,
					test_cnt);
			store_result(predict_test_label,res_path,test_cnt);
			print_accuracy(predict_test_label,test_benchmark_path,test_cnt);	
		}
		else
		{
			String train_list_path=args[0];
			String test_list_path=args[1];
			String[] feature_words = 
				{"之", "其", "或", "亦", "方", "于", "即", "皆", "因", "仍", "故", "尚", "呢", "了",
				"的", "着", "一", "不", "乃", "呀", "吗", "咧", "啊", "吧", "让", "向", "往", "是",
				"在", "越", "再", "更", "比", "很", "偏", "别", "好", "可", "便", "就", "但", "儿",
				"又", "也", "都", "要", "这", "那", "你", "我", "他", "来", "去", "道", "说"
				};
			String train_arff_path="train.arff";
			String test_arff_path="test.arff";
			String res_path="HW2_1600012819.txt";
			
			int feature_num=feature_words.length;
			init_global(feature_num);
			construct_train_feature(train_list_path,feature_words);
			construct_test_feature(test_list_path,feature_words);
			create_train_arff(train_arff_path,feature_words);
			create_test_arff(test_arff_path,feature_words);
			int test_cnt=category_size_0+category_size_1+category_size_2
					-train_size_0-train_size_1-train_size_2;
			int[] predict_test_label=classify_test_data(train_arff_path,test_arff_path,
					test_cnt);
			store_result(predict_test_label,res_path,test_cnt);
		}
		
	}
}
