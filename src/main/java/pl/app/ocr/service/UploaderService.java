package pl.app.ocr.service;

import com.jcraft.jsch.*;
import org.springframework.stereotype.Service;

@Service
public class UploaderService {

    private static final String REMOTE_HOST = "******";
    private static final String USERNAME = "ubuntu";
    private static final String PASSWORD = "******";
    private static final int REMOTE_PORT = 22;
    private static final int SESSION_TIMEOUT = 10000;
    private static final int CHANNEL_TIMEOUT = 5000;

    public void uploadFile() {
        String localFile = "src/main/resources/images/test.jpeg";
        String remoteFile = "/home/ubuntu/upload/test.jpeg";

        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts("/home/pawel/.ssh/known_hosts");
            jschSession = jsch.getSession(USERNAME, REMOTE_HOST, REMOTE_PORT);

            // authenticate using private key
//             jsch.addIdentity("/home/pawel/.ssh/id_rsa");

            // authenticate using password
            jschSession.setPassword(PASSWORD);

            // 10 seconds session timeout
            jschSession.connect(SESSION_TIMEOUT);

            Channel sftp = jschSession.openChannel("sftp");

            // 5 seconds timeout
            sftp.connect(CHANNEL_TIMEOUT);

            ChannelSftp channelSftp = (ChannelSftp) sftp;

            // transfer file from local to remote server
            channelSftp.put(localFile, remoteFile);

            // download file from remote server to local
//             channelSftp.get(remoteFile, localFile);

            channelSftp.exit();

        } catch (JSchException | SftpException e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

        System.out.println("Done");
    }
}
