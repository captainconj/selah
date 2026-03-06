#!/usr/bin/env python3
"""Export Helsinki-NLP/opus-mt-en-he to ONNX for diamond-onnxrt inference.

Produces:
  models/opus-mt-en-he/
    encoder_model.onnx
    decoder_model.onnx
    decoder_with_past_model.onnx
    source.spm, target.spm
    vocab.json, tokenizer_config.json, special_tokens_map.json

One-time setup:
  pip install optimum[onnxruntime] transformers sentencepiece

Usage:
  python scripts/export-marian-onnx.py
"""

import os
from pathlib import Path
from optimum.onnxruntime import ORTModelForSeq2SeqLM
from transformers import MarianTokenizer

MODEL_ID = "Helsinki-NLP/opus-mt-en-he"
OUTPUT_DIR = Path("models/opus-mt-en-he")

def main():
    print(f"Exporting {MODEL_ID} to ONNX...")
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

    # Export model to ONNX (encoder + decoder + decoder_with_past)
    model = ORTModelForSeq2SeqLM.from_pretrained(MODEL_ID, export=True)
    model.save_pretrained(OUTPUT_DIR)
    print(f"ONNX models saved to {OUTPUT_DIR}")

    # Save tokenizer (includes source.spm, target.spm, vocab.json)
    tokenizer = MarianTokenizer.from_pretrained(MODEL_ID)
    tokenizer.save_pretrained(OUTPUT_DIR)
    print(f"Tokenizer saved to {OUTPUT_DIR}")

    # Verify files exist
    expected = ["encoder_model.onnx", "decoder_model.onnx",
                "source.spm", "target.spm"]
    for f in expected:
        path = OUTPUT_DIR / f
        if path.exists():
            size_mb = path.stat().st_size / (1024 * 1024)
            print(f"  {f}: {size_mb:.1f} MB")
        else:
            print(f"  WARNING: {f} not found!")

    print("\nDone. Load in Selah with (translate/load!)")

if __name__ == "__main__":
    main()
