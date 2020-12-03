package com.miraclesoft.datalake.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.model.SkipToken;
import com.miraclesoft.datalake.repository.SkipTokenRepository;

@Service
public class SkipTokenService {

	@Autowired
	private SkipTokenRepository skipTokenrepo;

	public SkipToken add(SkipToken skiptoken) {
		skipTokenrepo.save(skiptoken);
		return skiptoken;
	}
	
	public SkipToken findSkiptoken(Long id) {
		return skipTokenrepo.findById(id).get();
	}
	
	public SkipToken findByString(String extractorname) {
		return skipTokenrepo.findByExtractorName(extractorname);
	}

	public SkipToken update(SkipToken skiptoken, Long id) {
		skiptoken.setId(id);
		return skipTokenrepo.save(skiptoken);
	}

	public Long delete(Long id) {
		skipTokenrepo.deleteById(id);
		return id;
	
	}

	public List<SkipToken> list() {
		return skipTokenrepo.findAll();
	}

}
