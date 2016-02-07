/*
 * Copyright 2016 UnboundID Corp.
 *
 * All Rights Reserved.
 */
package com.unboundid.ops.broker.models;

/**
 * Store adapter status.
 *
 * @author Jacob Childress <jacob.childress@unboundid.com>
 */
public class StoreAdapterStatus
{
  private final String name;
  private final boolean available;


  /**
   * Constructs a store adapter status instance.
   *
   * @param name
   *          The store adapter name.
   * @param available
   *          Whether or not the store adapter is available.
   */
  public StoreAdapterStatus(String name, boolean available)
  {
    this.name = name;
    this.available = available;
  }


  /**
   * Gets the store adapter name.
   *
   * @return The store adapter name.
   */
  public String getName()
  {
    return name;
  }


  /**
   * Whether or not the store adapter is available.
   *
   * @return The store adapter availability.
   */
  public boolean isAvailable()
  {
    return available;
  }


  /** {@inheritDoc} */
  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    StoreAdapterStatus that = (StoreAdapterStatus) o;

    return available == that.available && name.equals(that.name);
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int result = name.hashCode();
    result = 31 * result + (available ? 1 : 0);
    return result;
  }
}