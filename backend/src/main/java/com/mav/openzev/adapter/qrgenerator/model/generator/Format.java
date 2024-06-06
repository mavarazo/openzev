package com.mav.openzev.adapter.qrgenerator.model.generator;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Format {
  @NonNull @Builder.Default private Language language = Language.DE;
  @NonNull @Builder.Default private OutputSize outputSize = OutputSize.QR_BILL_ONLY;
  @NonNull @Builder.Default private SeparatorType separatorType = SeparatorType.NONE;
  @NonNull @Builder.Default private GraphicsFormat graphicsFormat = GraphicsFormat.SVG;

  public enum Language {
    DE,
    FR,
    IT,
    EN
  }

  public enum OutputSize {
    A4_PORTRAIT_SHEET,
    QR_BILL_ONLY
  }

  public enum SeparatorType {
    NONE,
    DASHED_LINE_WITH_SCISSORS
  }

  public enum GraphicsFormat {
    PDF,
    SVG
  }
}
