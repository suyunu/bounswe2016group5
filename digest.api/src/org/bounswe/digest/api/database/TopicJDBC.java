package org.bounswe.digest.api.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

import org.bounswe.digest.api.database.model.Comment;
import org.bounswe.digest.api.database.model.Question;
import org.bounswe.digest.api.database.model.Quiz;
import org.bounswe.digest.api.database.model.Topic;
import org.bounswe.digest.api.database.model.TopicManager;
import org.bounswe.digest.api.database.model.TopicTag;
import org.bounswe.digest.api.database.model.User;

import com.google.gson.Gson;
//import com.mysql.cj.api.jdbc.Statement;
import java.sql.Statement;
import java.sql.Timestamp;

public class TopicJDBC {
	public static int createTopic(Topic topic) {
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return -1;
		}
		PreparedStatement statement = null;
		int result = 0;
		String query = "INSERT INTO topic (header, image, body, owner, status) VALUES (?, ?, ?, ?, ?, ?)";
		try {
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, topic.getHeader());
			// statement.setString(2, type);
			statement.setString(2, topic.getImage());
			//statement.setString(3, topic.getUrl());
			statement.setString(4, topic.getBody());
			statement.setInt(5, topic.getOwner());
			statement.setInt(6, 0); // Doktor bu ne?
			statement.executeUpdate();

			int tid = -1;
			ResultSet resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				tid = resultSet.getInt(1);
			}

			ArrayList<TopicTag> tags = topic.getTags();
			for (TopicTag tag : tags) {
				PreparedStatement tagStatement = null;
				String tagQuery = "INSERT INTO topic_tag (tid,tag) VALUES (?,?)";
				try {
					connection.setAutoCommit(false);
					tagStatement = connection.prepareStatement(tagQuery);
					tagStatement.setInt(1, tid);
					tagStatement.setString(2, tag.getTag());
					tagStatement.executeUpdate();
					
				} catch (SQLException e) {
					result = -1;
					e.printStackTrace();
					try {
						System.err.print("Transaction is being rolled back");
						connection.rollback();
					} catch (SQLException excep) {
						excep.printStackTrace();

					}

				} finally {
					if (statement != null) {
						try {
							statement.close();
						} catch (SQLException e) {
							result = -1;
							e.printStackTrace();
						}
					}
					try {
						connection.setAutoCommit(true);
					} catch (SQLException e) {
						result = -1;
						e.printStackTrace();
					}
				}

			}
		} catch (SQLException e) {
			result = -1;
			e.printStackTrace();
			try {
				System.err.print("Transaction is being rolled back");
				connection.rollback();
			} catch (SQLException excep) {
				excep.printStackTrace();

			}

		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					result = -1;
					e.printStackTrace();
				}
			}
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				result = -1;
				e.printStackTrace();
			}
		}
		ConnectionPool.close(connection);
		return result;
	}

	public static String getTopicsWithUser(int uid) {
		String query = "SELECT * FROM digest.topic WHERE topic.owner=(?)";
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "";
		}
		PreparedStatement statement = null;
		ArrayList<Topic> result = new ArrayList<Topic>();
		ResultSet resultSet;
		try {
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(query);
			statement.setInt(1, uid);
			resultSet = statement.executeQuery();
			
			//public Topic(int id, String header, String image, String body, int owner, int status,
					//ArrayList<TopicTag> tags, ArrayList<Quiz> quizzes, ArrayList<String> media, ArrayList<Comment> comments, Timestamp timestamp) {
			while (resultSet.next()) {
				result.add(new Topic(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
						resultSet.getString(4),
						resultSet.getInt(5), resultSet.getInt(6), null, null, null, null, resultSet.getTimestamp(7)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				System.err.print("Transaction is being rolled back");
				connection.rollback();
			} catch (SQLException excep) {
				excep.printStackTrace();
			}
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		ConnectionPool.close(connection);
		for(Topic t: result){
			int tid=t.getId();
			t.setTags(getTagsOfTopic(tid));
			t.setComments(getCommentsArrayOfTopic(tid));
			t.setQuizzes(getQuizArrayOfTopic(tid));
		}
		Gson gson = new Gson();
		return gson.toJson(result);
	}
	public static String getRecentTopics(int count) {
		String query = "SELECT * FROM digest.topic ORDER BY timestamp DESC LIMIT ?";
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "";
		}
		PreparedStatement statement = null;
		ArrayList<Topic> result = new ArrayList<Topic>();
		ResultSet resultSet;
		try {
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(query);
			statement.setInt(1, count);
			resultSet = statement.executeQuery();
			
			//public Topic(int id, String header, String image, String body, int owner, int status,
					//ArrayList<TopicTag> tags, ArrayList<Quiz> quizzes, ArrayList<String> media, ArrayList<Comment> comments, Timestamp timestamp) {
			while (resultSet.next()) {
				result.add(new Topic(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
						resultSet.getString(4),
						resultSet.getInt(5), resultSet.getInt(6), null, null, null, null, resultSet.getTimestamp(7)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				System.err.print("Transaction is being rolled back");
				connection.rollback();
			} catch (SQLException excep) {
				excep.printStackTrace();
			}
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		ConnectionPool.close(connection);
		for(Topic t: result){
			t.setTags(getTagsOfTopic(t.getId()));
		//	t.setQuizzes(get);
		}
		Gson gson = new Gson();
		return gson.toJson(result);
	}
	
	private static ArrayList<TopicTag> getTagsOfTopic(int tid){
		String query = "SELECT * FROM digest.topic_tag  WHERE topic_tag.id=?";
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return new ArrayList<TopicTag>();
		}
		PreparedStatement statement = null;
		ArrayList<TopicTag> tags = new ArrayList<TopicTag>();
		ResultSet resultSet;
		try {
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(query);
			statement.setInt(1, tid);
			resultSet = statement.executeQuery();
			while(resultSet.next()){
				tags.add(new TopicTag(resultSet.getInt(1), resultSet.getInt(2), resultSet.getString(3)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				System.err.print("Transaction is being rolled back");
				connection.rollback();
			} catch (SQLException excep) {
				excep.printStackTrace();
			}
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		ConnectionPool.close(connection);
		return tags;
	}

	private static int addQuiz(Quiz quiz) {
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return -1;
		}
		PreparedStatement statement = null;
		int qid = -1;
		String query = "INSERT INTO quiz (name) VALUES (?)";
		try {
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, quiz.getName());
			statement.executeUpdate();
			ResultSet resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				qid = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				System.err.print("Transaction is being rolled back");
				connection.rollback();
			} catch (SQLException excep) {
				excep.printStackTrace();

			}
			ConnectionPool.close(connection);
			return -1;
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
					ConnectionPool.close(connection);
					return -1;
				}
			}
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
				ConnectionPool.close(connection);
				return -1;
			}
		}
		ConnectionPool.close(connection);

		for (Question q : quiz.getQuestions()) {
			ArrayList<String> choices = q.getChoices();
			ArrayList<Integer> answers = q.getAnswers();
			int questionId = addQuestion(q.getText());
			for (int i = 0; i < choices.size(); i++) {
				int cid = addChoice(choices.get(i), answers.contains(i) ? 1 : 0);
				addQuestionChoice(questionId, cid);
			}
			addQuizQuestion(qid, questionId);
		}

		return qid;
	}
	
	private static ArrayList<Quiz> getQuizArrayOfTopic(int tid){
		String query = "SELECT quiz.*, question.*, choice.text, choice.isAnswer "
					 + "FROM quiz, question, choice, digest.quiz_question, question_choice, topic_quiz "
					 + "WHERE quiz.id=quiz_question.quiz_id AND question.id=quiz_question.question_id AND "
					 + "question.id=digest.question_choice.qid AND choice.id=question_choice.cid AND "
					 + "topic_quiz.tid=?;";
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return new ArrayList<Quiz>();
		}
		PreparedStatement statement = null;
		ResultSet resultSet;
		ArrayList<Quiz> result=new ArrayList<Quiz>();
		try {
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(query);
			statement.setInt(1, tid);
			resultSet = statement.executeQuery();
			
			HashSet<Integer> quiz=new HashSet<Integer>();
			HashSet<Integer> question=new HashSet<Integer>();
			while (resultSet.next()) {
				int quizId=resultSet.getInt(1);
				if(!quiz.contains(quizId)){
					quiz.add(quizId);
					result.add(new Quiz(resultSet.getString(2), new ArrayList<Question>()));
				}
				int questionId=resultSet.getInt(3);
				ArrayList<Question> questions=result.get(result.size()-1).getQuestions();
				if(!question.contains(questionId)){
					question.add(questionId);
					questions.add(new Question(resultSet.getString(4), new ArrayList<String>(), new ArrayList<Integer>()));
				}
				questions.get(questions.size()-1).getChoices().add(resultSet.getString(5));
				if(resultSet.getInt(6)==1){
					questions.get(questions.size()-1).getAnswers().add(questions.get(questions.size()-1).getChoices().size()-1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				System.err.print("Transaction is being rolled back");
				connection.rollback();
			} catch (SQLException excep) {
				excep.printStackTrace();
			}
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		ConnectionPool.close(connection);

		return result;
	}
	
	public static String getQuizzesOfTopic(int tid){
		Gson gson=new Gson();
		return gson.toJson(getQuizArrayOfTopic(tid));
	}
	
	
	private static int addQuizQuestion(int quizId, int questionId) {
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return -1;
		}
		PreparedStatement statement = null;
		int result = -1;
		String query = "INSERT INTO quiz_question (quiz_id, question_id) VALUES (?,?)";
		try {
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.setInt(1, quizId);
			statement.setInt(2, questionId);
			statement.executeUpdate();
			ResultSet resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				result = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			result = -1;
			e.printStackTrace();
			try {
				System.err.print("Transaction is being rolled back");
				connection.rollback();
			} catch (SQLException excep) {
				excep.printStackTrace();

			}

		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					result = -1;
					e.printStackTrace();
				}
			}
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				result = -1;
				e.printStackTrace();
			}
		}
		ConnectionPool.close(connection);
		return result;
	}

	private static int addQuestionChoice(int qid, int cid) {
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return -1;
		}
		PreparedStatement statement = null;
		int result = -1;
		String query = "INSERT INTO question_choice (qid,cid) VALUES (?,?)";
		try {
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.setInt(1, qid);
			statement.setInt(2, cid);
			statement.executeUpdate();
			ResultSet resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				result = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			result = -1;
			e.printStackTrace();
			try {
				System.err.print("Transaction is being rolled back");
				connection.rollback();
			} catch (SQLException excep) {
				excep.printStackTrace();

			}

		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					result = -1;
					e.printStackTrace();
				}
			}
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				result = -1;
				e.printStackTrace();
			}
		}
		ConnectionPool.close(connection);
		return result;
	}

	private static int addQuestion(String text) {
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return -1;
		}
		PreparedStatement statement = null;
		int result = -1;
		String query = "INSERT INTO question (text) VALUES (?)";
		try {
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, text);
			statement.executeUpdate();
			ResultSet resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				result = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			result = -1;
			e.printStackTrace();
			try {
				System.err.print("Transaction is being rolled back");
				connection.rollback();
			} catch (SQLException excep) {
				excep.printStackTrace();

			}

		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					result = -1;
					e.printStackTrace();
				}
			}
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				result = -1;
				e.printStackTrace();
			}
		}
		ConnectionPool.close(connection);
		return result;
	}

	private static int addChoice(String c, int isAnswer) {
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return -1;
		}
		PreparedStatement statement = null;
		int result = -1;
		String query = "INSERT INTO choice (text,isAnswer) VALUES (?, ?)";
		try {
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, c);
			statement.setInt(2, isAnswer);
			statement.executeUpdate();
			ResultSet resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				result = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			result = -1;
			e.printStackTrace();
			try {
				System.err.print("Transaction is being rolled back");
				connection.rollback();
			} catch (SQLException excep) {
				excep.printStackTrace();

			}

		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					result = -1;
					e.printStackTrace();
				}
			}
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				result = -1;
				e.printStackTrace();
			}
		}
		ConnectionPool.close(connection);
		return result;
	}

	public static int addQuizToTopic(int tid, Quiz quiz) {
		int qid = addQuiz(quiz);
		return addTopicQuiz(tid, qid);
	}

	private static int addTopicQuiz(int tid, int qid) {
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return -1;
		}
		PreparedStatement statement = null;
		int result = 0;
		String query = "INSERT INTO topic_quiz (tid,qid) VALUES (?, ?)";
		try {
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(query);
			statement.setInt(1, tid);
			statement.setInt(2, qid);
			statement.executeUpdate();
		} catch (SQLException e) {
			result = -1;
			e.printStackTrace();
			try {
				System.err.print("Transaction is being rolled back");
				connection.rollback();
			} catch (SQLException excep) {
				excep.printStackTrace();

			}

		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					result = -1;
					e.printStackTrace();
				}
			}
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				result = -1;
				e.printStackTrace();
			}
		}
		ConnectionPool.close(connection);
		return result;
	}
	// give -1 if there is no upper comment of this comment.
	public static int addComment(String body, int uid,  int upperCommentId, int tid) {
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return -1;
		}
		PreparedStatement statement = null;
		int result = 0;
		// what is the default value of rate?
		String query = "INSERT INTO comment (body, uid, ucid, tid) VALUES (?, ?, ?, ?)";
		try {
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(query);
			statement.setString(1, body);
			statement.setInt(2, uid);
			statement.setInt(3, upperCommentId);
			statement.setInt(4, tid);
			statement.executeUpdate();
		} catch (SQLException e) {
			result = -1;
			e.printStackTrace();
			try {
				System.err.print("Transaction is being rolled back");
				connection.rollback();
			} catch (SQLException excep) {
				excep.printStackTrace();
				
			}
			
		}finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					result = -1;
					e.printStackTrace();
				}
			}
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				result = -1;
				e.printStackTrace();
			}
		}
		ConnectionPool.close(connection);
		return result;
	}
	public static String getCommentsOfTopic(int tid){
		Gson gson = new Gson();
		return gson.toJson(getCommentsArrayOfTopic(tid));
	}
	private static ArrayList<Comment> getCommentsArrayOfTopic(int tid){
		String query="SELECT id, body, uid, tid, ucid, rate, timestamp FROM comment WHERE comment.tid=(?)";
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return new ArrayList<Comment>();
		}
		PreparedStatement statement = null;
		ArrayList<Comment> result=new ArrayList<Comment>();
		ResultSet resultSet;
		try {
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(query);
			statement.setInt(1,tid);
			resultSet=statement.executeQuery();
			//public Topic(int id, String header, String type, String image, String url, String body, 
			//int owner, int status,ArrayList<TopicManager> topicManagers, ArrayList<TopicTag> tags)
			while(resultSet.next()){
				result.add(new Comment(resultSet.getInt(1),resultSet.getString(2),resultSet.getInt(3),
						resultSet.getInt(4),resultSet.getInt(5), resultSet.getInt(6), resultSet.getTimestamp(7)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				System.err.print("Transaction is being rolled back");
				connection.rollback();
			} catch (SQLException excep) {
				excep.printStackTrace();	
			}
		}finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		ConnectionPool.close(connection);
		return result;
	}

	public static String getTopic(int tid) {
		String query = "SELECT * FROM digest.topic WHERE topic.id=?";
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "";
		}
		PreparedStatement statement = null;
		Topic result=null;
		ResultSet resultSet;
		try {
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(query);
			statement.setInt(1, tid);
			resultSet = statement.executeQuery();
			
			// public Topic(int id, String header, String type, String image,
			// String url, String body,
			// int owner, int status,ArrayList<TopicManager> topicManagers,
			// ArrayList<TopicTag> tags)
			if (resultSet.next()) {
				result = (new Topic(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
						resultSet.getString(4),
						resultSet.getInt(5), resultSet.getInt(6), null, null, null, null, resultSet.getTimestamp(7)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				System.err.print("Transaction is being rolled back");
				connection.rollback();
			} catch (SQLException excep) {
				excep.printStackTrace();
			}
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		ConnectionPool.close(connection);
		if(result!=null){
			result.setTags(getTagsOfTopic(tid));
			result.setComments(getCommentsArrayOfTopic(tid));
			result.setQuizzes(getQuizArrayOfTopic(tid));
		}
		
		return result.printable();
	}
}
