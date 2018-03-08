package com.openmind.intellij.extension;

import java.util.List;
import java.util.Properties;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.openmind.intellij.action.ScrollToClassFileAction;
import com.openmind.intellij.action.UploadFileToS3Action;
import com.openmind.intellij.helper.AmazonS3Helper;
import com.openmind.intellij.helper.FileHelper;


public class S3UploadPluginPostStartupActivity implements StartupActivity {

    public void runActivity(@NotNull Project project) {

        ActionManager am = ActionManager.getInstance();
        DefaultActionGroup group = (DefaultActionGroup) am.getAction("S3UploadPlugin.Menu");

        if (group == null) {
            return;
        }

        final Properties customProperties = FileHelper.getCustomProperties(project); // metto in startup?
        AmazonS3Helper.setCustomProperties(customProperties);

        // add UploadFileToS3Actions
        List<String> versionFiles = AmazonS3Helper.getVersionFiles(project);
        AmazonS3Helper.setSingleProject(versionFiles.size() == 1);

        for(String versionFile : versionFiles) {
            String versionFileName = versionFile.substring(0, versionFile.lastIndexOf('.'));
            AnAction action = new UploadFileToS3Action(versionFileName, versionFile);
            am.registerAction("S3UploadPlugin.UploadAction" + versionFileName, action);
            group.add(action);
        }

        // add ScrollToClassFileAction
        AnAction scrollToClassFileAction = new ScrollToClassFileAction("Scroll to .class");
        am.registerAction("S3UploadPlugin.ScrollToClassFile", scrollToClassFileAction);
        group.add(scrollToClassFileAction);

    }
}