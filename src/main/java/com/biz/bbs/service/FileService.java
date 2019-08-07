package com.biz.bbs.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.biz.bbs.mapper.FileDao;
import com.biz.bbs.model.BBsVO;
import com.biz.bbs.model.FileVO;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class FileService {

	@Autowired
	FileDao fDao;
	
	private String upLoadFolder = "c:/bizwork/upload/";
	/*
	 * 파일들의 이름을 UUID 이름으로 변경하고
	 * 변경된 파일들의 이름을 FileVO에 담아서
	 * 리턴 하는 metgod 
	 */
	
	
	public void uploadFileList(BBsVO bbsVO) {
		
	
		
		// 1. VO에서 파일 리스트를 추출
		List<MultipartFile> files = bbsVO.getBbs_files();
		// 2. VO에서 seq 값 추출
		long bbs_seq = bbsVO.getBbs_seq();
		
		
		// (업로드된)파일의 개수만큼 반복문 수행
		for(MultipartFile file : files) {
			
			// 4. 파일정보에서 원래 파일이름 추출
			String originName =file.getOriginalFilename();
			
			// 5. 파일이름에 UUID를 추가하여
			String uuString= UUID.randomUUID().toString();
			
			// 6. UUID와 원래파일이름을 연결해서
			// 7. 저장하는 파일 이름으로 생성
			String savaName = uuString + "_" +originName;
			
			// 8. 파일 리스트를 생성
			// 파일 테이블에 저장하기 위한 List를 생성
		
			
			// 업로드될 폴더 + 업로드될 UUID 파일이름을 묶어서 File 객체로 생성
			File uploadFile = new File(upLoadFolder, savaName);
			
			
			
				try {
					
					// 서버의 폴더에 저장하기
					file.transferTo(uploadFile);
					
					
					fDao.insert(FileVO.builder()
							.file_bbs_seq(bbs_seq) // tbl_bbs와 tbl_bbs_file 을 연결하는 key
							.file_name(savaName) // view에서 확인할 파일명
							.file_origin_name(originName).build()); // 원래 원본 파일이름
						
				} catch (IllegalStateException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
			
		}
	
	}
	
	public List<FileVO> uploads(MultipartHttpServletRequest files) {
		List<MultipartFile> fileList = files.getFiles("files");
		List<FileVO> fileVOList = new ArrayList<FileVO>();
		for(MultipartFile file : fileList) {
			fileVOList.add(FileVO.builder()
			.file_origin_name(file.getOriginalFilename())
			.file_name(this.upLoad(file)).build());
		}
		return fileVOList;
	}
	
	public String upLoad(MultipartFile file) {
		
		if(file.isEmpty() || file == null) return null;
		
		String originName = file.getOriginalFilename();
		String uuString = UUID.randomUUID().toString();
		String savaName = uuString + "_" + originName;
		
		File saverDir = new File(upLoadFolder);
		if(!saverDir.exists()) {
			saverDir.mkdir();
		}
		
		File saveFile = new File(upLoadFolder,savaName);
		try {
			file.transferTo(saveFile);
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return savaName;
	}


	public boolean file_delete(long file_seq) {
		
		// 1. 파일 정보 추출
		FileVO fileVO = fDao.findBySeq(file_seq);
		// 2. 파일의 물리적 경로 생성
		File delFlie = new File(upLoadFolder, fileVO.getFile_name());
		
		// 3. 파일이 있는지 확인 한 후 
		if(delFlie.exists()) {
			// 4. 파일 삭제
			delFlie.delete();
			
			// 5. DB 정보 삭제
			fDao.delete(file_seq);
			
			return true;
		}
		return false;
	}
	
	
	
}
