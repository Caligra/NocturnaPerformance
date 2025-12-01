package com.nocturna.performance.dto.mediaurl.repository;

import com.nocturna.performance.dto.mediaurl.MediaURL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaURLRepository extends JpaRepository<MediaURL, String> {
}
