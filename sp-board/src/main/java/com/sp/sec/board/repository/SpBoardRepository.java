package com.sp.sec.board.repository;

import com.sp.sec.board.domain.SpBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpBoardRepository extends MongoRepository<SpBoard, String> {

    Page<SpBoard> findAllByOpenOrderByCreatedDesc(boolean open, Pageable pageable);

}
