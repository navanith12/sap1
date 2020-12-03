//package com.miraclesoft.datalake.model;
//
//import java.io.Serializable;
//
//import javax.persistence.Column;
//import javax.persistence.Embeddable;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//
//import org.hibernate.annotations.GenericGenerator;
//import org.hibernate.annotations.Parameter;
//
//import com.miraclesoft.datalake.mongo.model.StringPrefixedSequenceIdGenerator;
//
//public class Sourcekey implements Serializable {
//	@Id
//	@Column(name = "id")
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private long id;
//	
//	@Column(name = "client")
//	private int client;
//	
//	@Column(name = "appserver")
//	private String applicationServer;
//
//
//	public Sourcekey() {
//	}
//
//	public long getId() {
//		return id;
//	}
//
//	public void setId(long id) {
//		this.id = id;
//	}
//
//	public int getClient() {
//		return client;
//	}
//
//	public void setClient(int client) {
//		this.client = client;
//	}
//
//	public String getApplicationServer() {
//		return applicationServer;
//	}
//
//	public void setApplicationServer(String applicationServer) {
//		this.applicationServer = applicationServer;
//	}
//
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((applicationServer == null) ? 0 : applicationServer.hashCode());
//		result = prime * result + client;
//		result = prime * result + (int) (id ^ (id >>> 32));
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Sourcekey other = (Sourcekey) obj;
//		if (applicationServer == null) {
//			if (other.applicationServer != null)
//				return false;
//		} else if (!applicationServer.equals(other.applicationServer))
//			return false;
//		if (client != other.client)
//			return false;
//		if (id != other.id)
//			return false;
//		return true;
//	}	
//	
//}
