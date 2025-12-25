import { StyleSheet } from "react-native";
import {
  colors,
  baseCard,
  baseStatusCard,
  baseStatusText,
  shadows,
  baseButton,
  baseButtonText,
} from "@/app/utils/theme";

export const styles = StyleSheet.create({
  screen: {
    flex: 1,
    marginTop: 40,
    margin: 20,
  },
  container: {
    flex: 1,
  },
  subtitle: {
    fontSize: 18,
    fontWeight: "600",
    marginTop: 20,
    marginBottom: 10,
  },
  statusCard: {
    ...baseCard,
    borderWidth: 2,
    flexDirection: "row",
    alignItems: "center",
  },
  statusCardSuccess: {
    ...baseStatusCard,
    borderColor: colors.success,
    backgroundColor: colors.successBg,
  },
  statusCardWarning: {
    ...baseStatusCard,
    borderColor: colors.warning,
    backgroundColor: colors.warningBg,
  },
  statusCardError: {
    ...baseStatusCard,
    borderColor: colors.error,
    backgroundColor: colors.errorBg,
  },
  statusLabel: {
    fontSize: 16,
    fontWeight: "600",
    marginRight: 10,
  },
  statusText: {
    fontSize: 16,
    fontWeight: "bold",
  },
  statusTextSuccess: {
    ...baseStatusText,
    color: colors.success,
  },
  statusTextWarning: {
    ...baseStatusText,
    color: colors.warning,
  },
  statusTextError: {
    ...baseStatusText,
    color: colors.error,
  },
  buttonRow: {
    flexDirection: "row",
    gap: 10,
    marginTop: 15,
    flexWrap: "wrap",
  },
  deviceList: {
    marginBottom: 20,
  },
  deviceCard: {
    ...baseCard,
    marginBottom: 10,
    flexDirection: "row",
    justifyContent: "space-between",
    ...shadows.small,
  },
  deviceInfo: {
    flex: 1,
    marginRight: 10,
  },
  deviceName: {
    fontSize: 16,
    fontWeight: "600",
    marginBottom: 5,
  },
  deviceAddress: {
    fontSize: 14,
    color: colors.gray,
  },
  emptyText: {
    textAlign: "center",
    color: colors.lightGray,
    marginTop: 20,
    fontSize: 16,
  },
  permissionCard: {
    ...baseCard,
    backgroundColor: colors.warningBg,
    borderWidth: 1,
    borderColor: colors.warning,
    alignItems: "center",
    justifyContent: "center",
  },
  permissionText: {
    fontSize: 14,
    fontWeight: "600",
    textAlign: "center",
    color: colors.gray,
    marginBottom: 10,
  },
  refreshButton: {
    ...baseButton,
    backgroundColor: colors.primary,
  },
  refreshButtonDisabled: {
    backgroundColor: colors.borderGray,
    opacity: 0.6,
  },
  refreshButtonText: {
    ...baseButtonText,
  },
  refreshButtonTextDisabled: {
    color: colors.disabledText,
  },
  actionButton: {
    ...baseButton,
    backgroundColor: colors.success,
    minWidth: 110,
  },
  connectButton: {
    ...baseButton,
    paddingVertical: 8,
    paddingHorizontal: 16,
    backgroundColor: colors.success,
    minWidth: 120,
  },
  connectButtonDisabled: {
    backgroundColor: colors.borderGray,
    opacity: 0.6,
  },
  connectButtonText: {
    ...baseButtonText,
  },
  connectButtonTextDisabled: {
    color: colors.disabledText,
  },
  deviceCardButton: {
    gap: 10,
    justifyContent: "center",
  },
});
