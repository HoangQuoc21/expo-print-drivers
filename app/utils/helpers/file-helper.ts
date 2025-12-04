import { Directory, File, Paths } from "expo-file-system";

const checkFileExists = (fileName: string) => {
  let result = false;
  try {
    const file = new File(Paths.cache, fileName);
    result = file.exists;
  } catch (error) {
    console.error("--> Error checking file existence:", error);
    result = false;
  }
  return result;
};

const getFileUri = (fileName: string) => {
  return new File(Paths.cache, fileName).uri;
};

const downloadFile = async (url: string, fileName: string) => {
  try {
    const file = await File.downloadFileAsync(url, new Directory(Paths.cache));
    file.rename(fileName);
    console.log("--> File downloaded to:", file.uri);
  } catch (error) {
    console.error("--> Error downloading file:", error);
  }
};

export const FileHelper = {
  checkFileExists,
  getFileUri,
  downloadFile,
};
