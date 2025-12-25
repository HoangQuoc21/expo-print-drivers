import { PropsWithChildren } from "react";
import { ScrollView, View } from "react-native";
import { StatusBar } from "expo-status-bar";
import { styles } from "./screen.styles";
import { SafeAreaView } from "react-native-safe-area-context";

enum ScreenLayout {
  fixed,
  scroll,
}

export const Screen = (props: PropsWithChildren<{ layout?: ScreenLayout }>) => {
  const { children, layout = ScreenLayout.fixed } = props;

  let result = children;

  if (layout === ScreenLayout.scroll) {
    result = <ScrollView style={styles.scrollContainer}>{result}</ScrollView>;
  }

  return (
    <SafeAreaView style={styles.screen}>
      {result}
      <StatusBar style="auto" />
    </SafeAreaView>
  );
};
