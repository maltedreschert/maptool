/*
 * This software Copyright by the RPTools.net development team, and
 * licensed under the Affero GPL Version 3 or, at your option, any later
 * version.
 *
 * MapTool Source Code is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU Affero General Public
 * License * along with this source Code.  If not, please visit
 * <http://www.gnu.org/licenses/> and specifically the Affero license
 * text at <http://www.gnu.org/licenses/agpl.html>.
 */
package net.rptools.maptool.model.library.addon;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.gamedata.DataStoreManager;
import net.rptools.maptool.model.gamedata.data.DataType;
import net.rptools.maptool.model.gamedata.data.DataValue;
import net.rptools.maptool.model.gamedata.data.DataValueFactory;
import net.rptools.maptool.model.library.data.LibraryData;

/** Class to access the data for an AddOnLibrary. */
public class AddOnLibraryData implements LibraryData {

  /** The property name space for this library. */
  private static final String DATA_TYPE = "addon:";

  /** The AddOnLibrary this data is for. */
  private final AddOnLibrary addOnLibrary;

  /** The name space for this library. */
  private final String dataNameSpace;

  /**
   * Creates a new AddOnLibraryData.
   *
   * @param addOnLibrary The AddOnLibrary the data is for.
   * @param dataNameSpace The namespace for the data.
   */
  AddOnLibraryData(AddOnLibrary addOnLibrary, String dataNameSpace) {
    this.addOnLibrary = addOnLibrary;
    this.dataNameSpace = dataNameSpace;
  }

  @Override
  public CompletableFuture<String> libraryName() {
    return addOnLibrary.getName();
  }

  @Override
  public CompletableFuture<Set<String>> getAllKeys() {
    return new DataStoreManager()
        .getDefaultDataStore()
        .getProperties(DATA_TYPE, dataNameSpace)
        .thenApply(p -> p.stream().map(DataValue::getName).collect(Collectors.toSet()));
  }

  @Override
  public CompletableFuture<DataType> getDataType(String key) {
    return getDataValue(key).thenApply(DataValue::getDataType);
  }

  @Override
  public CompletableFuture<Boolean> isDefined(String key) {
    return getDataValue(key).thenApply(p -> !p.isUndefined());
  }

  @Override
  public CompletableFuture<DataValue> getValue(String key) {
    return getDataValue(key);
  }

  @Override
  public CompletableFuture<Void> setData(DataValue value) {
    return new DataStoreManager()
        .getDefaultDataStore()
        .setProperty(DATA_TYPE, dataNameSpace, value)
        .thenAccept(p -> {});
  }

  @Override
  public CompletableFuture<Void> setLongData(String name, long value) {
    return setData(DataValueFactory.fromLong(name, value));
  }

  @Override
  public CompletableFuture<Void> setDoubleData(String name, double value) {
    return setData(DataValueFactory.fromDouble(name, value));
  }

  @Override
  public CompletableFuture<Void> setBooleanData(String name, boolean value) {
    return setData(DataValueFactory.fromBoolean(name, value));
  }

  @Override
  public CompletableFuture<Void> setStringData(String name, String value) {
    return setData(DataValueFactory.fromString(name, value));
  }

  @Override
  public CompletableFuture<Void> setJsonArrayData(String name, JsonArray value) {
    return setData(DataValueFactory.fromJsonArray(name, value));
  }

  @Override
  public CompletableFuture<Void> setJsonObjectData(String name, JsonObject value) {
    return setData(DataValueFactory.fromJsonObject(name, value));
  }

  @Override
  public CompletableFuture<Void> setAssetData(String name, Asset value) {
    return setData(DataValueFactory.fromAsset(name, value));
  }

  /**
   * Returns the data value for the given key.
   *
   * @param key The key to get the data value for.
   * @return The data value for the given key.
   */
  private CompletableFuture<DataValue> getDataValue(String key) {
    return new DataStoreManager().getDefaultDataStore().getProperty(DATA_TYPE, dataNameSpace, key);
  }

  /**
   * Initializes the data for this library, only needs to be called once but safe to call multiple
   * times.
   *
   * @return a CompletableFuture that completes when the data is initialized.
   */
  CompletableFuture<Void> initialize() {
    var ds = new DataStoreManager().getDefaultDataStore();
    return ds.createNamespace(DATA_TYPE, dataNameSpace);
  }
}
