package com.tistory.hskimsky.jcommander.converter;

import com.beust.jcommander.IStringConverter;
import com.tistory.hskimsky.jcommander.CloneSpec;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * FileConverter
 *
 * @author Haneul, Kim
 */
public class CloneSpecConverter implements IStringConverter<List<CloneSpec>> {

  @Override
  public List<CloneSpec> convert(String value) {
    Yaml yaml = new Yaml(new Constructor(CloneSpec.class));
    try {
      List<CloneSpec> cloneSpecs = StreamSupport
        .stream(yaml.loadAll(new FileReader(value)).spliterator(), false)
        .map(x -> (CloneSpec) x)
        .collect(Collectors.toList());
      cloneSpecs.forEach(CloneSpec::validate);
      return cloneSpecs;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }
}
