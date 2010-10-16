package com.jklas.search.index.dto;

import java.io.Serializable;

import com.jklas.search.SearchEngine;
import com.jklas.search.configuration.SearchConfiguration;
import com.jklas.search.configuration.SearchMapping;
import com.jklas.search.exception.SearchEngineMappingException;
import com.jklas.search.index.IndexId;

public class IndexObjectDto implements Serializable {

	private static final long serialVersionUID = -7985869692765549281L;

	public static final Serializable NO_ID = new Serializable() {
		private static final long serialVersionUID = 1L;
		public int hashCode(){ return 0; }
		public boolean equals(Object other){ return this == other; }
	};

	private Serializable id;

	private final Object entity;

	private IndexId indexId = IndexId.getDefaultIndexId();

	public IndexObjectDto(Object entity) {
		SearchConfiguration configuration = SearchEngine.getInstance().getConfiguration();

		if(!SearchEngine.getInstance().isConfigured()) throw new IllegalStateException("Can't figure out object id when there's no active configuration");

		SearchMapping mapping = configuration.getMapping(entity);

		// if this is a container, we defer the id extraction to the pipeline
		if(!mapping.isIndexable() && mapping.isIndexableContainer()) {
			this.id = NO_ID;
		} else {
			try {
				this.id = (Serializable)mapping.extractId(entity);
			} catch (SearchEngineMappingException e) {
				throw new RuntimeException("Couldn't get object id",e);
			}			
		}
		
		this.entity = entity;
	}

	public IndexObjectDto(Object entity, Serializable id) {
		this.id = id;
		this.entity = entity;
	}


	public Serializable getId() {
		return id;
	}

	public Object getEntity() {
		return entity;
	}

	public void setId(Serializable id) {
		this.id = id;
	}

	public void setIndexId(IndexId indexId) {
		this.indexId = indexId;
	}

	public IndexId getIndexId() {
		return indexId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entity.getClass() == null) ? 0 : entity.getClass().hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((indexId == null) ? 0 : indexId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndexObjectDto other = (IndexObjectDto) obj;
		if (entity.getClass() == null) {
			if (other.entity.getClass() != null)
				return false;
		} else if (!entity.getClass().equals(other.entity.getClass()))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (indexId == null) {
			if (other.indexId != null)
				return false;
		} else if (!indexId.equals(other.indexId))
			return false;
		return true;
	}
	
	@Override
	public String toString() {	
		return "ID: "+getId()+"\nEntity:"+getEntity().toString();
	}

}
