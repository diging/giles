package edu.asu.giles.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.files.IFilesManager;

@Controller
public class ViewUploadController {
	
	@Autowired
	private IFilesManager filesManager;

	@RequestMapping(value="/uploads/{uploadId}")
	public String showUploadPage(@PathVariable("uploadId") String uploadId, Model model) {
		IUpload upload = filesManager.getUpload(uploadId);
		List<IFile> files = filesManager.getFilesByUploadId(uploadId);
		
		model.addAttribute("upload", upload);
		model.addAttribute("files", files);
		
		return "uploads/upload";
	}
}
