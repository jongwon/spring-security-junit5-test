package com.sp.sec.board.service;

import com.sp.sec.board.domain.Comment;
import com.sp.sec.board.domain.SpBoard;
import com.sp.sec.board.domain.SpBoardSummary;
import com.sp.sec.board.repository.SpBoardRepository;
import com.sp.sec.user.domain.User;
import com.sp.sec.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpBoardService {

    private final MongoTemplate mongoTemplate;
    private final SpBoardRepository boardRepository;
    private final UserService userService;

    // 1. 저장
    public SpBoard save(SpBoard board){
        if(StringUtils.isEmpty(board.getBoardId())){
            board.setCreated(LocalDateTime.now());
        }
        board.setUpdated(LocalDateTime.now());
        return boardRepository.save(board);
    }

    // 2. 리스트 페이징
    public Page<SpBoardSummary> list(int pageNum, int size){
        Page<SpBoard> boardPage = boardRepository.findAllByOpenOrderByCreatedDesc(true,
                PageRequest.of(pageNum - 1, size));
        Map<String, User> userMap = userService.getUserMap(boardPage.getContent().stream().map(
                board->board.getWriterId()).collect(Collectors.toSet()));
        return boardPage.map(board->SpBoardSummary.of(board, userMap.get(board.getWriterId())));
    }

    // 3. 수정
    private boolean update(String boardId, Update update){
        update.set("updated", LocalDateTime.now());
        return mongoTemplate.updateFirst(Query.query(Criteria.where("boardId").is(boardId)),
                update, SpBoard.class).wasAcknowledged();
    }

    // 3.1 컨텐츠, 타이틀, 컨텐츠-타이틀,
    public boolean updateContent(String boardId, String content){
        return update(boardId, Update.update("content", content));
    }
    public boolean updateTitle(String boardId, String title){
        return update(boardId, Update.update("title", title));
    }
    public boolean update(String boardId, String title, String content){
        Update update = Update.update("title", title);
        update.set("content", content);
        return update(boardId, update);
    }

    // 4. 가져오기
    public Optional<SpBoard> findBoard(String boardId){
        return boardRepository.findById(boardId).map(board->{
            userService.findUser(board.getWriterId()).ifPresent(
                    user->board.setWriter(user)
            );
            return board;
        });
    }

    // 5. 삭제
    public Optional<SpBoard> removeBoard(String boardId){
        return findBoard(boardId).map(board->{
            boardRepository.delete(board);
            return board;
        });
    }

    // 6. 코멘트 추가
    public Comment addComment(String boardId, Comment comment){
        comment.setCommentId(ObjectId.get().toHexString());
        comment.setCreated(LocalDateTime.now());
        Update update = new Update();
        update.push("commentList", comment);
        mongoTemplate.updateFirst(Query.query(Criteria.where("boardId").is(boardId)),
                update, SpBoard.class);
        return comment;
    }

    public boolean removeComment(String boardId, String commentId){
        Update update = new Update();
        update.pull("commentList", Query.query(Criteria.where("commentId").is(commentId)));
        return mongoTemplate.updateFirst(Query.query(Criteria.where("boardId").is(boardId)),
                update, SpBoard.class).wasAcknowledged();
    }

    // 7. 리스트 클리어 (테스트용)
    public void clearBoards(){
        boardRepository.deleteAll();
    }

}
