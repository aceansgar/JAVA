import java.io.*;
import java.net.*;
import java.util.*;

// ####
class Score {
	private long correct = 0;
	private long total = 0;

	public void update(boolean hit) {
		total++;
		if (hit)
			correct++;
	}

	public long getTotal() {
		return total;
	}

	public double getRatio() {
		if (total > 0) {
			return (double)correct/total;
		} else {
			return -1;
		}
	}

	public void clear() {
		correct = 0;
		total = 0;
	}
}

// ####
class Question {
	private String id;
	private long time;
	private List<Integer> ans;
	public Question(String id, long time, List<Integer> ans) {
		this.id = id;
		this.time = time;
		this.ans = ans;
	}
	public int getAns(int i) {
		if (i < ans.size()) {
			return ans.get(i);
		} else {
			return -1;
		}
	}
}

// ####
class ChapterList
{
	private ArrayList<String> txtList;
	private ArrayList<Integer> tagList;

	public ChapterList() {
		txtList = new ArrayList<String>();
		tagList = new ArrayList<Integer>();
	}

	public void add(String bookname, int chapter_begin, int chapter_end, String encoding, int tag) {
		for (int i = chapter_begin; i <= chapter_end; i++) {
			String txt = readTxt(bookname, i, encoding);
			if (txt != null) {
				txtList.add(txt);
				tagList.add(tag);
			}
		}
	}

	public void add(ChapterList list, int i0, int i1) {
		txtList.addAll(list.getTxtList().subList(i0, i1));
		tagList.addAll(list.getTagList().subList(i0, i1));
	}

	public void add(ChapterList list) {
		add(list, 0, list.size());
	}

	private String readTxt(String name, int chapter, String encoding) {
		try {
			File f = new File(encoding+"/"+name, chapter+".txt");
			InputStreamReader isr = new InputStreamReader(new FileInputStream(f), encoding);
			BufferedReader reader = new BufferedReader(isr);
			StringBuilder txt = new StringBuilder();
			String line = "";
			while ((line = reader.readLine()) != null) {
				txt.append(line);
			}
			reader.close();
			isr.close();
			return txt.toString();
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public void permutate() {
		Random rand = new Random();
		for (int i = 0; i < size(); i++) {
			String tmpTxt = txtList.get(i);
			int tmpTag = tagList.get(i);
			int j = rand.nextInt(size());
			txtList.set(i, txtList.get(j));
			tagList.set(i, tagList.get(j));
			txtList.set(j, tmpTxt);
			tagList.set(j, tmpTag);
		}
	}

	public ArrayList<String> getTxtList() {
		return txtList;
	}

	public ArrayList<Integer> getTagList() {
		return tagList;
	}

	public int size() {
		return txtList.size();
	}
}

// ####
class CallManager extends Thread
{
	private static String[] studentList = { 
		"000000"
	};

	private static Hashtable<String, Question> pendingQuestions = new Hashtable<String, Question>();
	private static Hashtable<String, Score> scoreBoard = new Hashtable<String, Score>();

	private Socket client;

	// ####
	CallManager(Socket client) {
			this.client = client;
	}

	private boolean findStudentID(String studentID) {
		if (studentID == null)
			return false;
		for (int i = 0; i < studentList.length; i++) {
			if (studentList[i].equals(studentID))
				return true;
		}
		return false;
	}

	private int[] randTag() {
		int[] tag = new int[3];
		tag[0] = 0; tag[1] = 1; tag[2] = 2;
		Random rand = new Random();
		for (int i = 0; i < 3; i++) {
			int tmp = tag[i];
			int j = rand.nextInt(3);
			tag[i] = tag[j];
			tag[j] = tmp;
		}
		return tag;
	}

	public void run() {
		try {
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
			Hashtable<String, Object> msg = (Hashtable<String, Object>)is.readObject();
			System.out.println("Msg from " + client.getInetAddress() + ": " + msg.toString());
			String method = (String)msg.get("method");
			String id = (String)msg.get("id");
			String result = "";
			if (method == null) {
				result = "no method";
			}
			else if (method.equals("initTest")) {
				result = initTest((String)msg.get("param1"), (String)msg.get("param2"));
			} 
			else if (method.equals("submit")) {
				result = submit((String)msg.get("param1"), (String)msg.get("param2"), (List)msg.get("param3"));
			}
			else if (method.equals("getScore")) {
				result = getScore((String)msg.get("param1"));
			}
			else if (method.equals("clearScore")) {
				result = clearScore((String)msg.get("param1"));
			}
			else {
				result = "unknown method";
			}

			msg.clear();
			msg.put("method", method);
			msg.put("result", result);
			msg.put("id", id);

			System.out.println("Sending result to " + client.getInetAddress() + ":" + msg.toString());

			Socket caller = new Socket(client.getInetAddress(), 6789);
			ObjectOutputStream os = new ObjectOutputStream(caller.getOutputStream());
			os.writeObject(msg);
			os.flush();
			caller.close();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public String initTest(String studentID, String encoding) {
		if (studentID == null || !findStudentID(studentID)) {
			return "unknown studentID " + studentID;
		}
		if (encoding == null || (!encoding.toUpperCase().equals("UTF-8") 
		                      && !encoding.toUpperCase().equals("GBK"))) {
			return "unknown encoding " + encoding;
		}

		for (int test = 0; test < 100; test++) {
			ChapterList[] list = {new ChapterList(), new ChapterList(), new ChapterList()};
			int[] tag = randTag();
			list[0].add("hlm", 1, 80, encoding, tag[0]); list[0].permutate();
			list[1].add("hlm", 81, 120, encoding, tag[1]); list[1].permutate();
			list[2].add("sgyy", 1, 120, encoding, tag[2]); list[2].permutate();

			ChapterList[] trainingSet = {new ChapterList(), new ChapterList(), new ChapterList()};
			trainingSet[0].add(list[0], 0, 20);
			trainingSet[1].add(list[1], 0, 20);
			trainingSet[2].add(list[2], 0, 20);

			ChapterList testingSet = new ChapterList();
			testingSet.add(list[0], 20, list[0].size());
			testingSet.add(list[1], 20, list[1].size());
			testingSet.add(list[2], 20, list[2].size());
			testingSet.permutate();

			long curTime = System.currentTimeMillis();
			String id = studentID + "_" + curTime + "_" + test;

			ArrayList<Integer> ans = testingSet.getTagList();
			Question q = new Question(id, curTime, ans);
			pendingQuestions.put(id, q);

			Hashtable<String, Object> msg = new Hashtable<String, Object>();
			msg.put("method", "test");
			for (int i = 0; i < 3; i++) {
				switch (tag[i]) {
				case 0: msg.put("param1", trainingSet[i].getTxtList()); break;
				case 1: msg.put("param2", trainingSet[i].getTxtList()); break;
				case 2: msg.put("param3", trainingSet[i].getTxtList()); break;
				}
			}
			msg.put("param4", testingSet.getTxtList());
			msg.put("id", id);

			try {
				Socket caller = new Socket(client.getInetAddress(), 6789);
				ObjectOutputStream os = new ObjectOutputStream(caller.getOutputStream());
				os.writeObject(msg);
				os.flush();
				caller.close();
			}
			catch (IOException e) {
				System.err.println(e.getMessage());
				return "Exception thrown when sending questions";
			}
		}

		return "Success";
	}

	public String submit(String studentID, String questionID, List<Integer> ans) {
		if (studentID == null || !findStudentID(studentID)) {
			return "unknown studentID " + studentID;
		}
		if (questionID == null || pendingQuestions.get(questionID) == null) {
			return "unknown question " + questionID;
		}
		if (scoreBoard.get(studentID) == null) {
			scoreBoard.put(studentID, new Score());
		}
		Question q = pendingQuestions.get(questionID);
		int nCorrect = 0;
		for (int i = 0; i < ans.size(); i++) {
			boolean correct = ans.get(i) == q.getAns(i);
			scoreBoard.get(studentID).update(correct);
			if (correct)
				nCorrect ++;
		}
		return String.valueOf(nCorrect);
	}

	public String getScore(String studentID) {
		if (studentID == null || !findStudentID(studentID)) {
			return "unknown studentID " + studentID;
		}
		if (scoreBoard.get(studentID) == null) {
			return "no score available";
		}
		double score = scoreBoard.get(studentID).getRatio();
		return String.valueOf(score);
	}

	public String clearScore(String studentID) {
		if (studentID == null || !findStudentID(studentID)) {
			return "unknown studentID " + studentID;
		}
		if (scoreBoard.get(studentID) == null) {
			return "no score";
		}
		scoreBoard.get(studentID).clear();
		return "Success";
	}
}

// ####
class Main
{
	public static void main(String args[]) {
		try {
			ServerSocket server = new ServerSocket(9876);
			while (true) {
				Socket call = server.accept();
				new CallManager(call).start();
			}
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}

/*

	public void clearScore(String studentID) {
		try {
			if (!findStudentID(studentID)) {
				throw new Exception("illegal call of clearScore: unknown studentID " + studentID);
			}
			synchronized (scoreBoard) {
				Score score = scoreBoard.get(studentID);
				if (score != null)
					score.clear();
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void getScore(String studentID) {
	}

				if (submission != null && submission.size() == ans.size()) {
					int correct = 0;
					for (int i = 0; i < submission.size(); i++) {
						if (submission.get(i) == ans.get(i))
							correct++;
					}
					scoreBoard.get(studentID).update(correct, submission.size());
				}
*/

