import { Text, View } from "react-native";
import React from "react";
import { styles } from "./app-title.styles";
import * as Application from "expo-application";

export const AppTitle = () => {
  return (
    <Text
      style={styles.title}
    >{`Bluetooth Printer (${Application.nativeApplicationVersion}+${Application.nativeBuildVersion})`}</Text>
  );
};
