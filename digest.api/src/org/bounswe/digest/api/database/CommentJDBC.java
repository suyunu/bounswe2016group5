package org.bounswe.digest.api.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bounswe.digest.api.database.model.Comment;

import com.google.gson.Gson;

public class CommentJDBC {
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
			String query = "INSERT INTO comment (body, uid, ucid, rate, tid) VALUES (?, ?, ?, 0, ?)";
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
		protected static ArrayList<Comment> getCommentsArrayOfTopic(int tid){
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

}
