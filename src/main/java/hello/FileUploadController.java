package hello;

import hello.storage.StorageFileNotFoundException;
import hello.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.io.IOUtils.copy;

@Controller
public class FileUploadController {

    public static final String JSON_PATH = "sigma/data.json";
    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/uploadForm")
    public String listUploadedFiles(Model model) throws IOException {
        List<String> lines = new ArrayList<>();
        model.addAttribute("files", lines);
        return "uploadForm";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        storageService.store(file);
        storageService.prepareJson();

        redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");
        return "redirect:/uploadForm";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/getJson", method = RequestMethod.GET)
    public void getFile(HttpServletResponse response) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(JSON_PATH);
            copy(is, response.getOutputStream());
            response.setContentType("application/json");
            response.flushBuffer();
        } catch (IOException ex) {
            throw new RuntimeException("IOError writing file to output stream");
        }
    }

}