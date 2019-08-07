package com.biz.bbs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biz.bbs.mapper.BBsDao;
import com.biz.bbs.model.BBsDto;
import com.biz.bbs.model.BBsVO;
import com.biz.bbs.model.FileVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BBsService {

	@Autowired
	BBsDao bDao;
	
	@Autowired
	FileService fileService;
	
	public List<BBsDto> bbsList() {
		//List<BBsVO> bbsList = bDao.selectAll();
		List<BBsDto> bbsList = bDao.selectAll();
		return bbsList;
	}

	public List<BBsDto> bbsListForFile() {
		List<BBsDto> bbsList = bDao.selectAllForFile();
		return bbsList;
	}
	/*
	 * 글쓰기를 수행할때 file을 같이 upload를 수행하면
	 * 해당하는 글의 bbs_seq를 조회하여
	 * file table에 저장할때 같이 저장을 수행 해줘야 한다.
	 * 
	 * 1. 게시글을 저장하고
	 * 2. 게시글의 bbs_seq를 조회하여
	 * 3. 파일정보를 insert할때 해당하는 bbs_seq를 같이 저장해주어야 한다.
	 * 그래야 게시글을 조회할때 해당하는 파일들을 같이 조회할수 있다
	 */
	public int insert(BBsVO bbsVO) {

		// 1. 게시글을 저장
		//		저장후에는 bbsVo.bbs_seq에 새로 생성된 PK값이 담겨있다
		int ret = bDao.insert(bbsVO);
		
		if(bbsVO.getBbs_files().size() > 0 && !bbsVO.getBbs_files().get(0).getOriginalFilename().isEmpty()) {
			/*
			 * 2. 게시글의
			 * 	가. bbs_seq가 추가되고
			 * 	나. UUID 파일이름이 추가된
			 * 	fileList를 생성하고
			 */
			fileService.uploadFileList(bbsVO);
			
			// 3. 그 리스트를 모두 insert 수행
			//fileService.fileListInsert(fileList);
			//long bbs_seq = bbsVO.getBbs_seq();
			//log.debug("게시판 SEQ : " + bbsVO.getBbs_seq());
			
		}
		return ret;
		}
	
	public int update(BBsVO bbsVO) {
		
		if(bbsVO.getBbs_files().size() > 0 && !bbsVO.getBbs_files().get(0).getOriginalFilename().isEmpty()) {
			
			fileService.uploadFileList(bbsVO);
			
			//fileService.fileListInsert(fileList);
		}	
		int ret = bDao.update(bbsVO);
		
		return ret;
	}	
	
	public int delete(long bbs_seq) {
		
		return 0;
	}

	public BBsDto getContent(long bbs_seq) {

		BBsDto bbsDto = bDao.findBySeqForFile(bbs_seq);
		return bbsDto;
	}
}
