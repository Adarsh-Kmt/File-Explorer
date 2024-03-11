package kamathadarsh.FileExplorer.service;

import kamathadarsh.Conduit.jooq.jooqGenerated.tables.records.FolderTableRecord;
import kamathadarsh.FileExplorer.JOOQRepository.JOOQFileRepository;
import kamathadarsh.FileExplorer.JOOQRepository.JOOQFolderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PathFinder {

    private final JOOQFileRepository fileRepository;
    private final JOOQFolderRepository folderRepository;


    public static String correctFilePath;
    public void depthFirstSearch(String currentPath, String fileName, int parentFolderId, int currentFolderId){


        if(parentFolderId == currentFolderId && fileRepository.checkIfFileExistsInCurrentFolder(fileName, currentFolderId)){

            correctFilePath = currentPath + "//" + fileName;
            System.out.println("finally the correct file path is: " + correctFilePath);
            return;
        }

        List<FolderTableRecord> potentialPaths = folderRepository.allSubFolderPathsInFolder(currentFolderId);

        if(potentialPaths.isEmpty()){

            System.out.println("no subFolders.");
            return;
        }

        for(FolderTableRecord subFolderRecord : potentialPaths){

            System.out.println("current subfolder is " + subFolderRecord.getFolderName());
            if(subFolderRecord.getFolderId() != null){
                System.out.println("DFS on " + subFolderRecord.getFolderName());
                depthFirstSearch(currentPath + "//" + subFolderRecord.getFolderName(), fileName, parentFolderId, subFolderRecord.getFolderId());
            }

        }
    }
    public String determinePathFromRootFolder(String fileName, int parentFolderId){

        correctFilePath = "root";

        depthFirstSearch("root", fileName, parentFolderId, 1);
        System.out.println(correctFilePath);
        return correctFilePath;
    }
}
