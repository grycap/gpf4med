/*
 * Copyright 2013 Institute for Molecular Imaging Instrumentation (I3M)
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by 
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 *   http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 * 
 * This product combines work with different licenses. See the "NOTICE" text
 * file for details on the various modules and licenses.
 * The "NOTICE" text file is part of the distribution. Any derivative works
 * that you distribute must include a readable copy of the "NOTICE" text file.
 */

package org.grycap.gpf4med.reflect;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.util.concurrent.Futures.successfulAsList;
import static java.util.Collections.synchronizedList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.grycap.gpf4med.cloud.DefaultNodeConfiguration.fromDefaults;
import static org.grycap.gpf4med.cloud.ovf.util.OVFFileUtils.numServers;
import static org.grycap.gpf4med.reflect.ProxyFactory.newResourceProxyClient;
import static org.grycap.gpf4med.Group.fromDefaultGroup;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.annotation.Nullable;
import javax.ws.rs.Path;

import org.grycap.gpf4med.Group;
import org.grycap.gpf4med.cloud.CloudService;
import org.grycap.gpf4med.model.util.LinkSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.net.HostAndPort;
import com.google.common.reflect.AbstractInvocationHandler;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * General purpose invocation handler that uses a thread pool to asynchronously 
 * execute calls to the underlying implementation.
 * @author Erik Torres <ertorser@upv.es>
 */
public class AsyncHandler<T> extends AbstractInvocationHandler {	

	private static final Logger LOGGER = LoggerFactory.getLogger(AsyncHandler.class);

	private static final long EXEC_TIMEOUT_MINUTES = 15;

	protected final Class<T> type;
	protected final ListeningExecutorService executorService;

	public AsyncHandler(final ListeningExecutorService executorService, final Class<T> type) {
		this.executorService = executorService;
		this.type = type;
	}

	@Override
	protected Object handleInvocation(final Object proxy, final Method method, final Object[] args)
			throws Throwable {
		final Group group = groupFromArgs(args, fromDefaultGroup());
		if ("create".equals(method.getName())) {			
			checkArgument(group != null, "Uninitialized group");
			// check and create group
			final Iterable<HostAndPort> servers = CloudService.INSTANCE.list(group.getGroup());
			checkState(servers == null || !servers.iterator().hasNext(), "A previous group exists");
			final int numServers = numServers(fromDefaults(), reportsCount(args), type);
			CloudService.INSTANCE.addServers(group.getGroup(), numServers);
		}
		final Iterable<HostAndPort> servers = CloudService.INSTANCE.list(group.getGroup());
		checkState(servers != null, "No servers were found in the group");
		// execute the operation on the servers
		final List<ListenableFuture<Object>> futures = synchronizedList(new ArrayList<ListenableFuture<Object>>());
		for (final Iterator<HostAndPort> it = servers.iterator(); it.hasNext();) {
			final ListenableFuture<Object> future = executorService.submit(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					final HostAndPort hostAndPort = it.next();					
					final String baseURL = "http://" + hostAndPort.getHostText() + ":" + hostAndPort.getPortOrDefault(80);
					final Path path = type.getAnnotation(Path.class);
					final T underlying = newResourceProxyClient(baseURL, path.value(), type);
					return method.invoke(underlying, args);
				}
			});
			futures.add(future);
		}
		// handle results
		final ListenableFuture<List<Object>> resultsFuture = successfulAsList(futures);
		List<Object> results = null;
		try {
			results = resultsFuture.get(MILLISECONDS.convert(EXEC_TIMEOUT_MINUTES, MINUTES), MILLISECONDS);
		} catch (InterruptedException | TimeoutException e) {
			LOGGER.warn("Failed to execute method " + method.getName() + " on group: " + group, e);
		} catch (ExecutionException e) {
			throw e.getCause();
		}
		if (method.getReturnType() == void.class) {
			return null;
		}		
		return mergeResults(results);		
	}

	private Object mergeResults(final List<Object> results) {
		Object merged = null;
		if (results != null && !results.isEmpty()) {
			if (results.size() == 1) {
				merged = results.get(0);
			} else {
				final Class<?> resultType = results.get(0).getClass();
				if (Byte.class.equals(resultType)) {
					byte total = 0;
					for (final Object item : results) {
						total += ((Byte)item).byteValue();
					}
					merged = (Byte)total;
				} else if (Short.class.equals(resultType)) {
					short total = 0;
					for (final Object item : results) {
						total += ((Short)item).shortValue();
					}
					merged = (Short)total;
				} else if (Integer.class.equals(resultType)) {
					int total = 0;
					for (final Object item : results) {
						total += ((Integer)item).intValue();
					}
					merged = (Integer)total;
				} else if (Long.class.equals(resultType)) {
					long total = 0l;
					for (final Object item : results) {
						total += ((Long)item).longValue();
					}
					merged = (Long)total;
				} else if (Float.class.equals(resultType)) {
					float total = 0.0f;
					for (final Object item : results) {
						total += ((Float)item).floatValue();
					}
					merged = (Float)total;
				} else if (Double.class.equals(resultType)) {
					double total = 0.0d;
					for (final Object item : results) {
						total += ((Double)item).doubleValue();
					}
					merged = (Double)total;
				} else if (resultType.isAssignableFrom(List.class)) {
					List<?> list = Lists.newArrayList();
					for (final Object item : results) {
						list = (List<?>) Iterables.concat(list, (List<?>) item);										
					}
					merged = list;
				} else if (resultType.isAssignableFrom(Map.class)) {
					Map<?, ?> map = Maps.newHashMap();
					for (final Object item : results) {
						map = (Map<?, ?>) Sets.union(map.entrySet(), ((Map<?, ?>) item).entrySet());
					}
					merged = map;
				} else {
					throw new UnsupportedOperationException("Cannot merge the type: " 
							+ resultType.getCanonicalName());
				}				
			}
		}
		return merged;
	}

	@Override
	public String toString() {
		return "Proxy of " + type.getCanonicalName();
	}

	public static Group groupFromArgs(final Object[] args, final @Nullable Group _default) {
		Group group = null;
		if (args != null) {
			for (int i = 0; i < args.length && group == null; i++) {
				if (args[i] instanceof Group) {
					group = Group.class.cast(args[i]);
				}
			}
		}
		return group != null ? group : _default;
	}

	public static boolean undefinedGroup(final Object[] args) {		
		return groupFromArgs(args, null) == null;
	}

	public static int reportsCount(final Object[] args) {
		LinkSet reports = null;
		if (args != null) {
			for (int i = 0; i < args.length && reports == null; i++) {
				if (args[i] instanceof LinkSet) {
					reports = LinkSet.class.cast(args[i]);
				}
			}
		}
		return reports != null ? reports.getLinks().size() : 0;
	}

}