package org.iii.SecBuzzer.template.controller.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.iii.SecBuzzer.template.controller.BaseController;
import org.iii.SecBuzzer.template.dao.SystemLogVariable;
import org.iii.SecBuzzer.template.domain.ArticleOrPictureData;
import org.iii.SecBuzzer.template.domain.CommonSetting;
import org.iii.SecBuzzer.template.domain.ResourceMessage;
import org.iii.SecBuzzer.template.domain.Subsystem;
import org.iii.SecBuzzer.template.domain.SystemLog;
import org.iii.SecBuzzer.template.service.ArticleOrPictureDataService;
import org.iii.SecBuzzer.template.service.CommonSettingService;
import org.iii.SecBuzzer.template.service.SubsystemService;
import org.iii.SecBuzzer.template.util.WebDatetime;
import org.iii.SecBuzzer.template.vo.WangEditorVO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.util.ClassUtil;
import com.google.api.client.util.Value;

import ch.qos.logback.core.util.FileUtil;

/**
 * 操作記錄控制器
 */
@Controller
@RequestMapping(value = "/sys/api", produces = "application/json; charset=utf-8")
public class s03_SystemLogController extends BaseController {

	@Autowired
	private SubsystemService subsystemService;
	
	@Autowired
	private ArticleOrPictureDataService articleOrPictureDataService;
	
	@Autowired
	private CommonSettingService  commonSettingService;
	
	
	private String targetControllerName = "sys";
	private String targetActionName = "s03";

	/**
	 * 取得System_Log資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
	 * @param json
	 *            查詢條件
	 * @return System_Log資料
	 */
	@PostMapping(value = "/s03/query")
	public String Query(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONObject listjson = new JSONObject();
		JSONArray sn_array = new JSONArray();

		if (menuService.getReadPermission(baseMemberId, targetControllerName, targetActionName)) {
			List<SystemLog> systemlogs = systemLogService.getList(json);
			if (systemlogs != null)
				for (SystemLog systemLog : systemlogs) {
					JSONObject sn_json = new JSONObject();
					sn_json.put("Id", systemLog.getId());
					sn_json.put("AppName", systemLog.getAppName());
					sn_json.put("FuncName", systemLog.getFuncName());
					sn_json.put("InputValue", systemLog.getInputValue());
					sn_json.put("ActionName", systemLog.getActionName());
					sn_json.put("Status", systemLog.getStatus());
					sn_json.put("Ip", systemLog.getIp());
					sn_json.put("HashCode", systemLog.getHashCode());
					sn_json.put("CreateAccount", systemLog.getCreateAccount());
					sn_json.put("CreateTime", WebDatetime.toString(systemLog.getCreateTime()));
					sn_array.put(sn_json);
				}
			listjson.put("total", systemLogService.getListSize(json));
			listjson.put("datatable", sn_array);
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
		} else {
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Deny, baseIpAddress, baseMemberAccount);
		}
		model.addAttribute("json", listjson.toString());
		return "msg";
	}
	@PostMapping(value = "/s03/queryList")
	public String QueryList(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONObject listjson = new JSONObject();
		
		List<Subsystem> subsystems = subsystemService.getList(json);
		JSONArray sn_array = new JSONArray();
		if (subsystems != null)
			for (Subsystem subsystem : subsystems) {
				JSONObject sn_json = new JSONObject();
				sn_json.put("Id", subsystem.getId());
				sn_json.put("Code", subsystem.getCode());
				sn_json.put("Name", subsystem.getName());
				sn_json.put("IconStyle", subsystem.getIconStyle());
				sn_json.put("IsEnable", subsystem.getIsEnable());
				sn_json.put("IsShow", subsystem.getIsShow());
				sn_json.put("Sort", subsystem.getSort());
				sn_array.put(sn_json);
			}
		listjson.put("datatable", sn_array);
		systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);

		model.addAttribute("json", listjson.toString());
		return "msg";
	}
	@PostMapping(value = "/s03/queryLocation")
	public String queryLocation(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONObject listjson = new JSONObject();
		
		List<CommonSetting> locationList = commonSettingService.getSetting(json);
		
		JSONArray sn_array = new JSONArray();
		if (locationList != null)
			for (CommonSetting commonSetting : locationList) {
				JSONObject sn_json = new JSONObject();
				sn_json.put("locationCode", commonSetting.getSetupCode());
				sn_json.put("locationDescription", commonSetting.getSetupDescription());
				sn_array.put(sn_json);
			}
		listjson.put("datatable", sn_array);
		systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);

		model.addAttribute("json", listjson.toString());
		return "msg";
	}
	
	@PostMapping(value = "/s03/submit")
	public @ResponseBody String Sumbit(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONObject responseJson = new JSONObject();
		CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
		if (token == null || token.getToken().equals("")) {
			return responseJson.toString();
		}
		
		articleOrPictureDataService.insert(json, baseMemberId);

		responseJson.put("msg", messageSource.getMessage("globalDataExist", null, locale));
		responseJson.put("success", true);
		systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Create, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
		
		return responseJson.toString();
	}
	
	
    private String insertPath = ClassUtils.getDefaultClassLoader().getResource("static/images").getPath();
	
    private String imagesPath = "http://localhost:8081/images/";
    
    private String cPath = "C:\\Users\\Eric\\Desktop\\Images\\";

    @PostMapping(value = "/s03/uploadImg")
    @ResponseBody
    public WangEditorVO uploadImg(Locale locale, HttpServletRequest request, Model model, 
    		@RequestParam("file") MultipartFile file) throws Exception {
        
    	//MultipartFile[] file = (MultipartFile[]) obj.get("file");
    	
    	//當file為空時
        if (file == null) {
            return WangEditorVO.error(1, "無圖片資訊");
        }
        /*String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        String name = UUID.randomUUID().toString();
        String filePath = request.getSession().getServletContext().getRealPath("images/")+ name + getSuffix(file) ;;
        uploadFile(file.getBytes(), filePath);*/
        
        
        
        
        //存入資料庫的檔案地址集合
        List<String> pathList = new ArrayList<>();
        //獲取檔名
        String name = UUID.randomUUID().toString();
        //拼接完整的 存放圖片地址，即：D:\\IO\\shop\\shopImage\\檔名.字尾名
        File path = new File(cPath + name + getSuffix(file)) ;
        //將圖片存放到path路徑下
        file.transferTo(path);
        //uploadFile(file.getBytes(), insertPath + name + getSuffix(file));
        //拼接完整的 訪問圖片地址，即：http://localhost:8888/檔名.字尾名
        pathList.add(cPath + name + getSuffix(file));
        
        
        return WangEditorVO.success(pathList);
    }
    public static void uploadFile(byte[] file, String filePath) throws Exception { 
    	File targetFile = new File(filePath); 
    	if(!targetFile.exists()){  
    	targetFile.mkdirs();  
    	}    
    	FileOutputStream out = new FileOutputStream(filePath);
    	out.write(file);
    	out.flush();
    	out.close();
    }

    /**
     * 獲取檔案的字尾名
     *
     * @param multipartFile 上傳的檔案
     * @return 檔案的字尾名
     */
    private String getSuffix(MultipartFile multipartFile) {
        //獲取完整的檔名
        String fileName = multipartFile.getOriginalFilename();
        //擷取字尾
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        return fileSuffix;
    }
}